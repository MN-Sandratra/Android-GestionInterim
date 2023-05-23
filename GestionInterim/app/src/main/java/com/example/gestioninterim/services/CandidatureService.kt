package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class CandidatureService : Service() {

    var candidature : CandidatureToSend? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("On commence ohhh")
        candidature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra("candidature", CandidatureToSend::class.java)
        } else {
            @Suppress("DEPRECATION") intent?.getSerializableExtra("candidature") as CandidatureToSend
        }

        val type = intent!!.getStringExtra("type")

        candidature?.let { sendPostRequest(it, type!!) }

        return START_STICKY
    }

    private fun sendPostRequest(candidature: CandidatureToSend, type: String?) {
        println("candidatures service")
        val cvFile = File(candidature.cv)
        val lmFile = File(candidature.lm)
        val client = OkHttpClient()

        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        candidature.javaClass.declaredFields.forEach { field ->
            field.isAccessible = true
            val propName = field.name
            val propValue = field.get(candidature)
            if (propValue != null && propValue.toString().isNotEmpty()) {
                if (propName == "cv" || propName == "lm") {
                    val file = if (propName == "cv") cvFile else lmFile
                    val fileRequestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
                    requestBodyBuilder.addFormDataPart(propName, file.name, fileRequestBody)
                } else {
                    requestBodyBuilder.addFormDataPart(propName, propValue.toString())
                }
            }
        }

        val requestBody = requestBodyBuilder.build()

        val request = Request.Builder()
            .url("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/$type")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println("URL : ${call.request().url}")
                    println("Response Code : ${response.code}")

                    response.body?.string()?.let { responseBody ->
                        println("Response : $responseBody")
                    }
                } else {
                    println("Request failed with response code ${response.code}")
                }
            }
        })
    }
}