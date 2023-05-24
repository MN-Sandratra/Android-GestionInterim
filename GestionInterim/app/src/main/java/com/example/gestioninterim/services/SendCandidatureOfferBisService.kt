package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Candidature
import com.example.gestioninterim.models.CandidatureToSend
import com.example.gestioninterim.resultEvent.CandidaturesResultInterimaireEvent
import com.example.gestioninterim.resultEvent.ValidationBooleanEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors



class SendCandidatureOfferBisService : Service() {

    var candidature : CandidatureToSend? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val identifiantOffre = intent!!.getStringExtra("identifiantOffre")

        candidature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra("candidature", CandidatureToSend::class.java)
        } else {
            @Suppress("DEPRECATION") intent?.getSerializableExtra("candidature") as CandidatureToSend?
        }

        sendPostRequestCandidature(candidature!!, identifiantOffre!!) { validation ->
            val event = ValidationBooleanEvent(validation)
            EventBus.getDefault().post(event)
        }

        return START_STICKY
    }

    private fun sendPostRequestCandidature(candidature: CandidatureToSend, identifiantOffre : String, callback: (validation: Boolean) -> Unit) {
        println("candidatures service")

        val client = OkHttpClient()

        Log.d("Affichage", "====> $candidature")
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        candidature.javaClass.declaredFields.forEach { field ->
            field.isAccessible = true
            val propName = field.name
            val propValue = field.get(candidature)

            if (propValue != null) {
                if (propName == "contenuCv" || propName == "contenuLm") {
                    val byteArray = propValue as ByteArray
                    val filename = if (propName == "contenuCv") candidature.cv else candidature.lm
                    val fileRequestBody = byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                    requestBodyBuilder.addFormDataPart(propName, filename, fileRequestBody)
                } else {
                    requestBodyBuilder.addFormDataPart(propName, propValue.toString())
                }
            }
        }

        requestBodyBuilder.addFormDataPart("identifiantOffre", identifiantOffre)

        val requestBody = requestBodyBuilder.build()

        val request = Request.Builder()
            .url("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/candidatureOffres/jobseekerOffre")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println("URL : ${call.request().url}")
                    println("Response Code : ${response.code}")

                    response.body?.string()?.let { responseBody ->
                        println("Response : $responseBody")
                    }
                    callback(true)
                } else {
                    println("Request failed with response code ${response.code}")
                    callback(false)
                }
            }
        })
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}



