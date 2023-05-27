package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.CandidatureEmployerResult
import com.example.gestioninterim.resultEvent.CandidaturesResultEvent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
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



class CandidatureResultService : Service() {


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val contact = intent!!.getStringExtra("contact")
        val type = intent!!.getStringExtra("type")
        val status = intent!!.getStringExtra("status")


        sendGetRequestOffers(contact, type, status) { candidatures ->
            val event = CandidaturesResultEvent(candidatures)
            EventBus.getDefault().post(event)
            Log.d("CANDIDATURES", "affichage => $candidatures")
        }



        return START_STICKY
    }

    fun sendGetRequestOffers(contact : String?, type : String?,  status : String?, callback: (candidatures: List<CandidatureEmployerResult>) -> Unit) {

        Executors.newSingleThreadExecutor().execute {

            // Je définis les paramètres de la requête
            val reqParamBuilder = StringBuilder()
            if(contact!!.contains("@")){
                reqParamBuilder.append("email=" + URLEncoder.encode(contact, "UTF-8"))
            }
            else{
                reqParamBuilder.append("telephone=" + URLEncoder.encode(contact, "UTF-8"))
            }

            if(!status.isNullOrEmpty()){
                reqParamBuilder.append("&status=" + URLEncoder.encode(status, "UTF-8"))
            }

            val reqParam = reqParamBuilder.toString()

            val mURL = URL("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/candidatureOffres/$type/?$reqParam")

            Log.d("Affichage", "=>> requete $mURL")

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
                        val offersType = object : TypeToken<List<CandidatureEmployerResult>>() {}.type
                        val candidatures: List<CandidatureEmployerResult> = gson.fromJson(response.toString(), offersType)
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



