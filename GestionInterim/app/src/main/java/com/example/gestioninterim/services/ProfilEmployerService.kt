package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ProfilEmployerService : Service() {

    lateinit var utilisateur: UtilisateurEmployeur

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        utilisateur = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent!!.getSerializableExtra("userRequest", UtilisateurEmployeur::class.java) as UtilisateurEmployeur
        } else {
            @Suppress("DEPRECATION") intent?.getSerializableExtra("userRequest") as UtilisateurEmployeur
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = Gson()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody: RequestBody = gson.toJson(utilisateur).toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/employers/${utilisateur.email1}")
            .put(requestBody)
            .build()

        Executors.newSingleThreadExecutor().execute {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // Handle the error
                }

                // Handle the response
                val responseBody = response.body?.string() // Get response as a string
                if (responseBody != null) {
                    val updatedUser: UtilisateurEmployeur = gson.fromJson(responseBody, UtilisateurEmployeur::class.java) // Convert the string to UtilisateurEmployeur
                    val event = ProfilEmployerResultEvent(updatedUser)
                    EventBus.getDefault().post(event)
                }
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
