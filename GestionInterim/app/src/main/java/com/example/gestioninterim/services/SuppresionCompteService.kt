package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.resultEvent.OffersResultEmployerEvent
import com.example.gestioninterim.resultEvent.ProfilEmployerResultEvent
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import java.net.URLEncoder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SuppresionCompteService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val contact = intent?.getStringExtra("contact")
        val type = intent?.getStringExtra("type")

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Log.d("Affichage", "JE SUIS DANS LE DELETE")

        val request = Request.Builder()
            .url("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/${type}/${contact}")
            .delete()
            .build()

        Executors.newSingleThreadExecutor().execute {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.d("Affichage", "JE SUIS DANS l'erreur !")

                }
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
