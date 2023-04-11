package com.example.gestioninterim.inscription

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import com.example.gestioninterim.R
import com.example.gestioninterim.login.LoginActivity
import com.example.gestioninterim.utilisateurAnonyme.MainAnonymeActivity
import com.google.android.material.button.MaterialButton

class InscriptionActivity : AppCompatActivity() {

    var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        // Définition du bouton pour passer l'inscription
        var skipButton = findViewById<ImageButton>(R.id.imageButton)

        // On ajoute le fragment au container
        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val fragment = FragmentChooseInscription()
        fragmentTransaction.add(R.id.containerInscription, fragment)
        fragmentTransaction.commit()

        // Listener sur le bouton de skip
        skipButton.setOnClickListener{
            val intent = Intent(this@InscriptionActivity, MainAnonymeActivity::class.java)
            startActivity(intent)
        }

    }
}