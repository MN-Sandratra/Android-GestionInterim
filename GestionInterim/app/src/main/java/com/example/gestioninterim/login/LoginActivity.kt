package com.example.gestioninterim.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.gestioninterim.inscription.InscriptionActivity
import com.example.gestioninterim.R
import com.example.gestioninterim.utilisateurAnonyme.MainAnonymeActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var toConnect:TextView = findViewById<TextView>(R.id.clickHere)

        val connectLater = findViewById<ImageButton>(R.id.imageButton)

        connectLater.setOnClickListener{
            val intent = Intent(this@LoginActivity, MainAnonymeActivity::class.java)
            startActivity(intent)
        }

        toConnect.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, InscriptionActivity::class.java)
            startActivity(intent)
        })
    }
}