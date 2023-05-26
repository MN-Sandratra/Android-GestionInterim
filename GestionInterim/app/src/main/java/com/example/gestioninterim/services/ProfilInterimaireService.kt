package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.resultEvent.OffersResultEmployerEvent
import com.example.gestioninterim.resultEvent.ProfilEmployerResultEvent
import com.example.gestioninterim.resultEvent.ProfilInterimaireResultEvent
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.net.URLEncoder
import java.nio.Buffer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ProfilInterimaireService : Service() {

    lateinit var utilisateur: UtilisateurInterimaire

    fun getRequestBodyContent(requestBody: RequestBody): String {
        return try {
            val buffer = okio.Buffer()
            requestBody.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "Erreur lors de la lecture du contenu du RequestBody"
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        utilisateur = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent!!.getSerializableExtra("userRequest", UtilisateurInterimaire::class.java) as UtilisateurInterimaire
        } else {
            @Suppress("DEPRECATION") intent?.getSerializableExtra("userRequest") as UtilisateurInterimaire
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = Gson()

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody: RequestBody = gson.toJson(utilisateur).toRequestBody(mediaType)

        var contact: String = if (!utilisateur.email.isNullOrEmpty()) {
            utilisateur.email ?: ""
        } else {
            utilisateur.phoneNumber ?: ""
        }

        val url = "http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/jobseekers/"

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        Log.d("Affichage", "url => $url")
        Log.d("Affichage", "body => ${getRequestBodyContent(requestBody)}")

        Executors.newSingleThreadExecutor().execute {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // Handle the error
                }

                // Handle the response
                val responseBody = response.body?.string() // Get response as a string
                if (responseBody != null) {
                    Log.d("Response", responseBody)
                    val updatedUser: UtilisateurInterimaire = gson.fromJson(responseBody, UtilisateurInterimaire::class.java) // Convert the string to UtilisateurEmployeur
                    val event = ProfilInterimaireResultEvent(updatedUser)
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
