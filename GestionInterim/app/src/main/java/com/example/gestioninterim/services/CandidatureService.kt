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
import java.io.File
import java.io.IOException

class CandidatureService : Service() {


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    var candidature : CandidatureToSend? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        candidature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra("candidature", CandidatureToSend::class.java)
        } else {
            @Suppress("DEPRECATION") intent?.getSerializableExtra("candidature") as CandidatureToSend?
        }

        sendPostRequestCandidature(candidature!!) { validation ->
            val event = ValidationBooleanEvent(validation)
            EventBus.getDefault().post(event)
        }

        return START_STICKY
    }

    private fun sendPostRequestCandidature(candidature: CandidatureToSend, callback: (validation: Boolean) -> Unit) {

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

        val requestBody = requestBodyBuilder.build()

        val request = Request.Builder()
            .url("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/candidatures")
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
}