package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.resultEvent.OffersResultEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

class OffresService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val latitude = intent?.getStringExtra("latitude")
        val longitude = intent?.getStringExtra("longitude")

        sendGetRequestOffers(latitude.toString(), longitude.toString()) { offers ->
            val event = OffersResultEvent(offers)
            EventBus.getDefault().post(event)
        }

        return START_STICKY
    }

    fun sendGetRequestOffers(latitude: String, longitude: String, callback: (offers: List<Offer>) -> Unit) {

        Executors.newSingleThreadExecutor().execute {

            // Je définis les paramètres de la requête
            val reqParam = "latitude=" + URLEncoder.encode(latitude, "UTF-8") +
                    "&longitude=" + URLEncoder.encode(longitude, "UTF-8")

            val mURL = URL("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/offers/?$reqParam")
            println("La requête est : $reqParam")

            with(mURL.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "GET"

                println("URL : $url")
                println("Response Code : $responseCode")
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
                        val offersType = object : TypeToken<List<Offer>>() {}.type
                        val offers: List<Offer> = gson.fromJson(response.toString(), offersType)
                        callback(offers)
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



