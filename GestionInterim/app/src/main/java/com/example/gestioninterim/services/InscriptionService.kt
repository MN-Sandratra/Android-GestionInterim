package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Utilisateur
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.Executors


class InscriptionService : Service() {

    var utilisateur : Utilisateur? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        utilisateur = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra("utilisateur", Utilisateur::class.java)
        } else {
            @Suppress("DEPRECATION") intent?.getSerializableExtra("utilisateur") as Utilisateur
        }

        val type = intent!!.getStringExtra("type")

        utilisateur?.let { sendPostRequest(it, type!!) }

        return START_STICKY
    }



    fun sendPostRequest(user: Utilisateur, type: String) {
        val client = OkHttpClient()
        var requestBody: RequestBody

        if(user is UtilisateurInterimaire) {
            var cvName: String? = user.cv

            val multipartBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            user.let {
                it.javaClass.declaredFields.forEach { field ->
                    field.isAccessible = true
                    val propName = field.name
                    val propValue = field.get(user)
                    if (propValue != null && propValue.toString().isNotEmpty()) {
                        if (propValue is ByteArray && propName == "contenuCv") {
                            val fileRequestBody = propValue.toRequestBody("application/pdf".toMediaTypeOrNull())
                            multipartBodyBuilder.addFormDataPart(propName, cvName, fileRequestBody)
                        } else {
                            multipartBodyBuilder.addFormDataPart(propName, propValue.toString())
                        }
                    }
                }
            }
            requestBody = multipartBodyBuilder.build()
        } else if(user is UtilisateurEmployeur) {
            val json = JsonObject()
            user.let {
                it.javaClass.declaredFields.forEach { field ->
                    field.isAccessible = true
                    val propName = field.name
                    val propValue = field.get(user)
                    if (propValue != null && propValue.toString().isNotEmpty()) {
                        json.addProperty(propName, propValue.toString())
                    }
                    // Pas d'ajout de propriété si propValue est null
                }
            }
            requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        } else {
            return  // Or handle this case as appropriate for your application.
        }

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


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}

