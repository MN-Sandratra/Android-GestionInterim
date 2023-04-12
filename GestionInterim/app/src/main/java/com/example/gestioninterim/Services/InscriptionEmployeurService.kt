package com.example.gestioninterim.Services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.models.UtilisateurInterimaire

class InscriptionEmployeurService : Service() {

    var utilisateur : UtilisateurEmployeur? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        utilisateur = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra("utilisateur", UtilisateurEmployeur::class.java)
        } else {
            @Suppress("DEPRECATION") intent?.getSerializableExtra("utilisateur") as UtilisateurEmployeur
        }


        Toast.makeText(this, "Le service est lancé", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}

