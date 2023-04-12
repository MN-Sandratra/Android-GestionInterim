package com.example.gestioninterim.Services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.gestioninterim.models.UtilisateurAgence
import com.example.gestioninterim.models.UtilisateurInterimaire

class InscriptionAgenceService : Service() {

    var utilisateur : UtilisateurAgence? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        utilisateur = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra("utilisateur", UtilisateurAgence::class.java)
        } else {
            @Suppress("DEPRECATION") intent?.getSerializableExtra("utilisateur") as UtilisateurAgence
        }


        Toast.makeText(this, "Le service est lancé", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}

