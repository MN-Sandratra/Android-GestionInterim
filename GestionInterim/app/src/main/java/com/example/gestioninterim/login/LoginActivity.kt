package com.example.gestioninterim.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gestioninterim.inscription.InscriptionActivity
import com.example.gestioninterim.R
import com.example.gestioninterim.resultEvent.LoginResultEvent
import com.example.gestioninterim.services.LoginService
import com.example.gestioninterim.utilisateurAnonyme.MainAnonymeActivity
import com.example.gestioninterim.utilisateurAgence.MainAgenceActivity
import com.example.gestioninterim.utilisateurAgence.SlidesActivityAgence
import com.example.gestioninterim.utilisateurEmployeur.MainEmployeurActivity
import com.example.gestioninterim.utilisateurEmployeur.SlidesActivity
import com.example.gestioninterim.utilisateurInterimaire.MainInterimaireActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var toInscription:TextView = findViewById<TextView>(R.id.clickHere)
        val connectLater = findViewById<ImageButton>(R.id.imageButton)

        val inputUsername = findViewById<TextInputEditText>(R.id.inputUsernameLogin)
        val inputPassword = findViewById<TextInputEditText>(R.id.inputPasswordLogin)
        val connectButton = findViewById<MaterialButton>(R.id.connectButton)

        // Connexion plus tard, redirection vers la page utilisateur anonyme
        connectLater.setOnClickListener{
            val intent = Intent(this@LoginActivity, MainAnonymeActivity::class.java)
            startActivity(intent)
        }

        // Redirection vers l'inscription
        toInscription.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, InscriptionActivity::class.java)
            startActivity(intent)
        })

        // Redirection vers l'utilisateur concerné
        connectButton.setOnClickListener{
            if(!(inputUsername.text?.isEmpty() == true || inputPassword.text?.isEmpty() == true)){
                    // Je lance le service
                    launchServiceLogin(inputUsername.text.toString(), hashPassword(inputPassword.text.toString()))
                }
            else{
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun launchServiceLogin(email: String, password: String?){
        val intent = Intent(this, LoginService::class.java)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        startService(intent)
    }


    fun hashPassword(password: String): String {
        try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val hash = messageDigest.digest(password.toByteArray(StandardCharsets.UTF_8))
            val hexString = StringBuilder()
            for (byte in hash) {
                val hex = String.format("%02X", byte)
                hexString.append(hex)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Error hashing password", e)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginResult(event: LoginResultEvent) {

        val sharedPreferences = getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()

        if(event.message != null){
            if(event.type == "jobSeeker"){
                val intent = Intent(this, MainInterimaireActivity::class.java)
                startActivity(intent)
            }
            else if (event.type == "employer"){
                if(event.hasSubscription == true){
                    val intent = Intent(this, MainEmployeurActivity::class.java)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this, SlidesActivity::class.java)
                    startActivity(intent)
                }
            }
            else if(event.type == "agence"){
                if(event.hasSubscription == true){
                    val intent = Intent(this, MainAgenceActivity::class.java)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this, SlidesActivityAgence::class.java)
                    startActivity(intent)
                }
            }
        }
        else{
            Toast.makeText(this, "Vos identifiants sont incorrects", Toast.LENGTH_SHORT).show()
        }
    }
}