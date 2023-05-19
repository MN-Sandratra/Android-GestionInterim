package com.example.gestioninterim.services;

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.OfferDAO
import com.example.gestioninterim.models.OfferEmployerDAO
import com.example.gestioninterim.resultEvent.AddOfferResultEvent
import com.example.gestioninterim.resultEvent.OffersResultEmployerEvent
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

class MissionEmployerService : Service(){

    lateinit var offersRequest : OfferEmployerDAO
    lateinit var offer : Offer


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

            offersRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent!!.getSerializableExtra("offerRequest", OfferEmployerDAO::class.java) as OfferEmployerDAO
            } else {
                @Suppress("DEPRECATION") intent?.getSerializableExtra("offerRequest") as OfferEmployerDAO
            }
            sendGetRequestOffers(offersRequest) { offers ->
                val event = OffersResultEmployerEvent(offers)
                EventBus.getDefault().post(event)
            }


        return START_STICKY
    }

    fun sendGetRequestOffers(offersRequest : OfferEmployerDAO, callback: (offers: List<Offer>) -> Unit) {

        Executors.newSingleThreadExecutor().execute {

            // Je définis les paramètres de la requête
            val reqParamBuilder = StringBuilder()
            offersRequest.email?.let { reqParamBuilder.append("email=" + URLEncoder.encode(it, "UTF-8") + "&") }
            offersRequest.metier?.let { reqParamBuilder.append("metier=" + URLEncoder.encode(it, "UTF-8") + "&") }
            offersRequest.dateDebut?.let { reqParamBuilder.append("dateDebut=" + URLEncoder.encode(it, "UTF-8") + "&") }
            offersRequest.dateFin?.let { reqParamBuilder.append("dateFin=" + URLEncoder.encode(it, "UTF-8") + "&") }

            // Supprimez le dernier caractère '&' si nécessaire
            if (reqParamBuilder.isNotEmpty() && reqParamBuilder.last() == '&') {
                reqParamBuilder.setLength(reqParamBuilder.length - 1)
            }

            val reqParam = reqParamBuilder.toString()


            val mURL = URL("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/offers/employers?$reqParam")
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



