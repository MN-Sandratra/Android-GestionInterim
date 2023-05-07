package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Abonnement
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.OfferDAO
import com.example.gestioninterim.resultEvent.AbonnementsResultEvent
import com.example.gestioninterim.resultEvent.OffersResultEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class AbonnementsService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val email = intent?.getStringExtra("email")
        val type = intent?.getStringExtra("type")

        println("L EMAIL EST $email")

        if(email != null){
            sendPostRequestAbonnement(type!!, email) { validation ->
                val event = AbonnementsResultEvent(validation = validation)
                EventBus.getDefault().post(event)
            }
        }
        else {
            sendGetRequestAbonnements() { abonnements ->
                val event = AbonnementsResultEvent(abonnements = abonnements)
                EventBus.getDefault().post(event)
            }
        }

        return START_STICKY
    }

    fun sendPostRequestAbonnement(type: String, email: String, callback: (validation: Boolean) -> Unit) {
        // Format current date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        // Add type, email, and current date to the request body
        val reqParam = StringBuilder()
            .append(URLEncoder.encode("type", "UTF-8"))
            .append('=')
            .append(URLEncoder.encode(type, "UTF-8"))
            .append('&')
            .append(URLEncoder.encode("email", "UTF-8"))
            .append('=')
            .append(URLEncoder.encode(email, "UTF-8"))
            .append('&')
            .append(URLEncoder.encode("date", "UTF-8"))
            .append('=')
            .append(URLEncoder.encode(currentDate, "UTF-8"))
            .toString()

        val mURL = URL("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/paiement/")

        Executors.newSingleThreadExecutor().execute {
            var validate = false
            with(mURL.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                val wr = OutputStreamWriter(outputStream)
                wr.write(reqParam)
                wr.flush()
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()
                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        val jsonResponse = JSONObject(response.toString())
                        validate = jsonResponse.getBoolean("validation") // Remplacez "success" par le nom réel du champ dans la réponse JSON
                    }
                }
            }
            callback(validate)
        }
    }

    fun sendGetRequestAbonnements(callback: (abonnements: List<Abonnement>) -> Unit) {

        Executors.newSingleThreadExecutor().execute {

            val mURL = URL("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/subscriptions/")

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
                        val abonnementsType = object : TypeToken<List<Abonnement>>() {}.type
                        val abonnements: List<Abonnement> = gson.fromJson(response.toString(), abonnementsType)
                        callback(abonnements)
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



