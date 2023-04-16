package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.example.gestioninterim.utilisateurInterimaire.MainInterimaireActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors


class InscriptionValidationService : Service() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val email = intent?.getStringExtra("email")
        val code = intent?.getStringExtra("code")

        // Si le code n'est pas null qu'on veut envoyer une requête avec un code
        // Sinon c'est qu'on redemande un code
        val isResendCode = code == null

        println("Le code est : $code")

        sendPostRequest(email.toString(), code?.toString(), isResendCode) { validate ->
            handler.post {
                if (validate) {
                    val intent = Intent(applicationContext, MainInterimaireActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
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


    fun sendPostRequest(email: String, code: String?, resendCode: Boolean = false, callback: (Boolean) -> Unit) {

        // Définition du endpoint
        val endpoint = if (resendCode) "code" else "validate"

        // J'ajoute l'email dans le corps de ma requête
        var reqParam = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")

        // Si je veux tenter la validation, j'ajoute le code
        if (!resendCode && code != null) {
            reqParam += "&" + URLEncoder.encode("validationCode", "UTF-8") + "=" + URLEncoder.encode(code, "UTF-8")
        }
        val mURL = URL("http://192.168.1.23:8000/api/validation/$endpoint")

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
                        validate = !resendCode
                    }
                }
            }
            callback(validate)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}

