package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Candidature
import com.example.gestioninterim.resultEvent.CandidaturesResultInterimaireEvent
import com.example.gestioninterim.resultEvent.ValidationBooleanEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors



class SendCandidatureOfferService : Service() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val identifiantCandidature = intent!!.getStringExtra("identifiantCandidature")
        val identifiantOffre = intent!!.getStringExtra("identifiantOffre")

        sendPostRequestOffers(identifiantCandidature, identifiantOffre) { validate ->
            val event = ValidationBooleanEvent(validate)
            EventBus.getDefault().post(event)
        }

        return START_STICKY
    }

    fun sendPostRequestOffers(identifiantCandidature : String?,  identifiantOffre : String?, callback: (validate: Boolean) -> Unit) {

        Executors.newSingleThreadExecutor().execute {

            // Je définis les paramètres de la requête
            val reqParam = JSONObject()
            reqParam.put("candidatureId", identifiantCandidature)
            reqParam.put("offreId", identifiantOffre)

            val postDataBytes = reqParam.toString().toByteArray(Charsets.UTF_8)

            val mURL = URL("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/candidatureOffres/")

            with(mURL.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "POST"

                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                outputStream.write(postDataBytes)

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = it.readText()

                        // Convert JSON response to a JSONObject
                        val responseObject = JSONObject(response)
                        val message = responseObject.optString("message")

                        callback(message == "Association candidature-offre créée avec succès")
                    }
                } else {
                    callback(false)
                }
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}



