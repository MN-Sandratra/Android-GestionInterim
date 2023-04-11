package com.example.gestioninterim.Services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import com.example.gestioninterim.models.Utilisateur
import java.io.Serializable

class InscriptionService : Service() {

    var utilisateur : Utilisateur? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        utilisateur = getSerializable(intent, "utilisateur", Utilisateur::class.java)
        Toast.makeText(this, "Le service est lancé", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }



    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun <T : Serializable?> getSerializable(intent: Intent?, key: String, className: Class<T>): T {
        return if (Build.VERSION.SDK_INT >= 33)
            intent?.getSerializableExtra(key, className)!!
        else
            intent?.getSerializableExtra(key) as T
    }


}

