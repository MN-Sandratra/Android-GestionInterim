package com.example.gestioninterim.services

import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.OfferDAO
import com.example.gestioninterim.models.OfferResult
import com.example.gestioninterim.resultEvent.AddOfferResultEvent
import com.example.gestioninterim.resultEvent.OffersResultEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

class OffresAgenceService : Service() {

    lateinit var offersRequest : OfferDAO
    lateinit var offer : Offer


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val isOffer = intent!!.getBooleanExtra("isOffer", false)

        if(isOffer){
            val gson = Gson()
            val jsonOffers = intent.getStringExtra("offerRequest")
            val listType: Type = object : TypeToken<List<Offer>>() {}.type
            val offers: List<Offer> = gson.fromJson(jsonOffers, listType)

            sendPostRequestOffer(offers) { success ->
                println(success)
                val event = AddOfferResultEvent(success)
                EventBus.getDefault().post(event)
            }
        }


//        if(isOffer){
//
//            // Récupérer liste des offres
//
//            sendPostRequestOffer(offer) { success ->
//                println(success)
//                val event = AddOfferResultEvent(success)
//                EventBus.getDefault().post(event)
//            }
//        }
//                else{
//            offersRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                intent!!.getSerializableExtra("offerRequest", OfferDAO::class.java) as OfferDAO
//            } else {
//                @Suppress("DEPRECATION") intent?.getSerializableExtra("offerRequest") as OfferDAO
//            }
//            sendGetRequestOffers(offersRequest) { offers ->
//                val event = OffersResultEvent(offers)
//                EventBus.getDefault().post(event)
//            }
//        }




        return START_STICKY
    }

    fun sendGetRequestOffers(offersRequest : OfferDAO, callback: (offers: List<OfferResult>) -> Unit) {

        Executors.newSingleThreadExecutor().execute {

            // Je définis les paramètres de la requête
            val reqParamBuilder = StringBuilder()
            offersRequest.metier?.let { reqParamBuilder.append("metier=" + URLEncoder.encode(it, "UTF-8") + "&") }
            offersRequest.ville?.let { reqParamBuilder.append("ville=" + URLEncoder.encode(it, "UTF-8") + "&") }
            offersRequest.latitude?.let { reqParamBuilder.append("latitude=" + URLEncoder.encode(it, "UTF-8") + "&") }
            offersRequest.longitude?.let { reqParamBuilder.append("longitude=" + URLEncoder.encode(it, "UTF-8") + "&") }
            reqParamBuilder.append("rayon=" + URLEncoder.encode(offersRequest.rayon.toString(), "UTF-8") + "&")
            offersRequest.dateDebut?.let { reqParamBuilder.append("dateDebut=" + URLEncoder.encode(it, "UTF-8") + "&") }
            offersRequest.dateFin?.let { reqParamBuilder.append("dateFin=" + URLEncoder.encode(it, "UTF-8") + "&") }

            // Supprimez le dernier caractère '&' si nécessaire
            if (reqParamBuilder.isNotEmpty() && reqParamBuilder.last() == '&') {
                reqParamBuilder.setLength(reqParamBuilder.length - 1)
            }

            val reqParam = reqParamBuilder.toString()


            val mURL = URL("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/offersAgence/?$reqParam")
            println("La requête est : $reqParam")

            Log.d("Affichage", "=> requete $reqParam")

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
                        val offersType = object : TypeToken<List<OfferResult>>() {}.type
                        val offers: List<OfferResult> = gson.fromJson(response.toString(), offersType)
                        callback(offers)
                    }
                } else {
                    callback(emptyList())
                }
            }
        }
    }

    fun sendPostRequestOffer(newOffers : List<Offer>, callback: (success: Boolean) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            val gson = Gson()
            val offersJson = gson.toJson(newOffers)

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            val body = offersJson.toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/offersAgence")
                .post(body)
                .build()

            val client = OkHttpClient()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    // Successfully created
                    callback(true)
                } else {
                    // Failed to create
                    val inputLine = response.body?.string()
                    println("Response error body : $inputLine")
                    callback(false)
                }
            }
        }
    }





    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}



