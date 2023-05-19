package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Utilisateur
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.resultEvent.LoginResultEvent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

class LoginService : Service(){

    private lateinit var sharedPreferences: SharedPreferences

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        sharedPreferences = getSharedPreferences("user_infos", MODE_PRIVATE)

        val email = intent?.getStringExtra("email")
        val password = intent?.getStringExtra("password")

        sendPostRequestLogin(email.toString(), password.toString()) { message, type, user, hasSubscription ->
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val jsonUser = gson.toJson(user)
            editor.putString("user", jsonUser)
            editor.apply()
            println(jsonUser)
            val event = LoginResultEvent(message, type, hasSubscription)
            EventBus.getDefault().post(event)
        }

        return START_STICKY
    }



    fun sendPostRequestLogin(email: String, password: String, callback: (message: String?, type: String?, user: Utilisateur?, hasSubscription: Boolean?) -> Unit) {
        val client = OkHttpClient()

        val mediaType = "application/x-www-form-urlencoded; charset=utf-8".toMediaType()
        val bodyContent = "email=${URLEncoder.encode(email, "UTF-8")}&password=${
            URLEncoder.encode(
                password,
                "UTF-8"
            )
        }"
        val body: RequestBody = bodyContent.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/login/")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, null, null, null)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        val jsonObject = JSONObject(responseBody)
                        val message = jsonObject.getString("message")
                        val type = jsonObject.getString("type")
                        val hasSubscription = jsonObject.getBoolean("hasSubscription")
                        val userJson = jsonObject.getJSONObject("user")

                        var user: Utilisateur? = null
                        val gson = GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create()

                        if (type == "employer") {
                            user =
                                gson.fromJson(userJson.toString(), UtilisateurEmployeur::class.java)
                        } else if (type == "jobSeeker") {
                            user = gson.fromJson(
                                userJson.toString(),
                                UtilisateurInterimaire::class.java
                            )
                        }

                        callback(message, type, user, hasSubscription)
                    } ?: run {
                        callback(null, null, null, null)
                    }
                } else {
                    callback(null, null, null, null)
                }
            }
        })
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}


