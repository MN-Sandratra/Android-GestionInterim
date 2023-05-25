package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Candidature
import com.example.gestioninterim.models.CandidatureEmployerResult
import com.example.gestioninterim.resultEvent.CandidaturesResultEvent
import com.example.gestioninterim.resultEvent.CandidaturesResultInterimaireEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors



class CandidatureJobSeekersResultService : Service() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val contact = intent!!.getStringExtra("contact")

        sendGetRequestOffers(contact) { candidatures ->
            val event = CandidaturesResultInterimaireEvent(candidatures)
            EventBus.getDefault().post(event)
        }

        return START_STICKY
    }

    fun sendGetRequestOffers(contact : String?,  callback: (candidatures: List<Candidature>) -> Unit) {

        Executors.newSingleThreadExecutor().execute {

            // Je définis les paramètres de la requête
            val reqParamBuilder = StringBuilder()
            if(contact!!.contains("@")){
                reqParamBuilder.append("email=" + URLEncoder.encode(contact, "UTF-8"))
            }
            else{
                reqParamBuilder.append("telephone=" + URLEncoder.encode(contact, "UTF-8"))
            }

            val reqParam = reqParamBuilder.toString()

            val mURL = URL("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/candidatures/jobseekers?$reqParam")

            with(mURL.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "GET"

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        // Convert JSON response to a list of Offer objects
                        val gson = Gson()
                        val offersType = object : TypeToken<List<Candidature>>() {}.type
                        val candidatures: List<Candidature> = gson.fromJson(response.toString(), offersType)
                        callback(candidatures)
                    }
                } else {
                    callback(emptyList())
                }
            }
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}



