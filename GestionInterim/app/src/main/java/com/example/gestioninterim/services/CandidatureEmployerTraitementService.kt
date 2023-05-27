package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.*
import com.example.gestioninterim.resultEvent.AbonnementsResultEvent
import com.example.gestioninterim.resultEvent.ValidationBooleanEvent
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.File
import java.io.IOException

class CandidatureEmployerTraitementService : Service() {


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val traitement = intent!!.getStringExtra("traitement")
        val candidatureId = intent!!.getStringExtra("identifiantCandidature")
        val offreId = intent!!.getStringExtra("identifiantOffre")

        Log.d("Affichage", "==> $traitement, $candidatureId, $offreId")
        sendPostRequestCandidature(traitement!!, candidatureId!!, offreId!!) { validation ->
            val event = ValidationBooleanEvent(validation)
            EventBus.getDefault().post(event)
        }

        return START_STICKY
    }

    private fun sendPostRequestCandidature(traitement: String, candidatureId : String, offreId : String, callback: (validation: Boolean) -> Unit) {
        println("candidatures service")

        val client = OkHttpClient()

        val json = "application/json; charset=utf-8".toMediaTypeOrNull()

        val body = JSONObject()
        body.put("status", traitement)
        body.put("candidatureId", candidatureId)
        body.put("offreId", offreId)

        val requestBody = body.toString().toRequestBody(json)

        val url = "http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/candidatureOffres/employersCandidature"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        Log.d("Affichage", "requête => $body")
        Log.d("Affichage", "URL => $url")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("Affichage", "===> ${call.request().url}, ${response.code}")
                    println("URL : ${call.request().url}")
                    println("Response Code : ${response.code}")

                    response.body?.string()?.let { responseBody ->
                        println("Response : $responseBody")
                    }
                    callback(true)
                } else {
                    Log.d("Affichage", "===> ${call.request().url}, ${response.code}")
                    println("Request failed with response code ${response.code}")
                    callback(false)
                }
            }
        })
    }

}