package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Utilisateur
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.resultEvent.AbonnementsResultEvent
import com.example.gestioninterim.resultEvent.ValidationInscriptionResultEvent
import com.example.gestioninterim.utilisateurInterimaire.MainInterimaireActivity
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


class InscriptionValidationService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedPreferences: SharedPreferences



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val email = intent?.getStringExtra("email")
        val code = intent?.getStringExtra("code")

        sharedPreferences = getSharedPreferences("user_infos", MODE_PRIVATE)


        // Si le code n'est pas null qu'on veut envoyer une requête avec un code
        // Sinon c'est qu'on redemande un code
        val isResendCode = code == null

        println("Le code est : $code")

        sendPostRequest(email.toString(), code?.toString(), isResendCode) { validate, type, user->
            handler.post {
                if (validate) {

                    val editor = sharedPreferences.edit()
                    val gson = Gson()
                    val jsonUser = gson.toJson(user)
                    editor.putString("user", jsonUser)
                    editor.apply()

                    val event = ValidationInscriptionResultEvent(type)
                    EventBus.getDefault().post(event)
                }
                else if (isResendCode) {
                    Toast.makeText(this, "Un nouveau code vient de vous être envoyé", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "Validation échouée", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return START_STICKY
    }


    fun sendPostRequest(email: String, code: String?, resendCode: Boolean = false, callback: (validation: Boolean, type: String, user: Any) -> Unit) {
        val client = OkHttpClient()

        // Définition du endpoint
        val endpoint = if (resendCode) "code" else "validate"

        // Construction du corps de la requête
        var requestBodyContent = "${URLEncoder.encode("email", "UTF-8")}=${URLEncoder.encode(email, "UTF-8")}"

        // Si je veux tenter la validation, j'ajoute le code
        if (!resendCode && code != null) {
            requestBodyContent += "&${URLEncoder.encode("validationCode", "UTF-8")}=${URLEncoder.encode(code, "UTF-8")}"
        }

        val mediaType = "application/x-www-form-urlencoded; charset=utf-8".toMediaType()
        val body: RequestBody = requestBodyContent.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/validation/$endpoint")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        val jsonResponse = JSONObject(responseBody)
                        val userType = jsonResponse.optString("type", "") // Remplacez "type" par le nom réel du champ dans la réponse JSON
                        val userJson = jsonResponse.optJSONObject("user") // Receives the 'user' object from the response
                        val validate = !resendCode
                        var user: Any = Any()

                        val gson = GsonBuilder()
                            .excludeFieldsWithoutExposeAnnotation()
                            .create()

                        println(userType.toString())
                            println(userJson.toString())
                        if (userType == "employers") {
                            user = gson.fromJson(userJson.toString(), UtilisateurEmployeur::class.java)
                        } else if (userType == "jobseekers") {
                            user = gson.fromJson(userJson.toString(), UtilisateurInterimaire::class.java)
                        }
                        else{
                            user = gson.fromJson(userJson.toString(), UtilisateurEmployeur::class.java)
                        }
                        callback(validate, userType, user)
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

