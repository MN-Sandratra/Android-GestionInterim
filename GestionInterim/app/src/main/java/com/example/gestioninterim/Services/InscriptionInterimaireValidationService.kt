package com.example.gestioninterim.Services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.utilisateurAnonyme.MainAnonymeActivity
import com.example.gestioninterim.utilisateurInterimaire.MainInterimaireActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


class InscriptionInterimaireValidationService : Service() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val email = intent?.getStringExtra("email")
        val code = intent?.getStringExtra("code")

        // Si le code n'est pas null qu'on veut envoyer une requête avec un code
        // Sinon c'est qu'on redemande un code
        if(code != null){
        sendPostRequestValidation(email.toString(), code.toString()) { validate ->
            handler.post {
                if (validate) {
                    val intent = Intent(applicationContext, MainInterimaireActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Validation échouée", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else{
            sendPostRequestResendCode(email.toString())
            Toast.makeText(this, "Un nouveau code vient de vous être envoyé", Toast.LENGTH_SHORT).show()
        }

        return START_STICKY
    }

    // Requête renvoie d'un code
    fun sendPostRequestResendCode(email: String) {


        Executors.newSingleThreadExecutor().execute {

            // J'ajoute à la requete les parameters si ils sont pas null ou vide ("")
            var reqParam = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")

            val mURL = URL("http://192.168.1.13:8000/api/jobseekers/code")
            with(mURL.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream);
                wr.write(reqParam);
                wr.flush();

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
                    }
                }
            }
        }
    }

    // Requête envoie d'un code
    fun sendPostRequestValidation(email: String, codeValidation : String,callback: (Boolean) -> Unit) {


        Executors.newSingleThreadExecutor().execute {

            var validate : Boolean = false

            // J'ajoute à la requete les parameters si ils sont pas null ou vide ("")
            var reqParam =
                URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
            reqParam += "&" + URLEncoder.encode(
                "validationCode",
                "UTF-8"
            ) + "=" + URLEncoder.encode(codeValidation, "UTF-8")

            val mURL = URL("http://192.168.1.13:8000/api/jobseekers/validate")
            with(mURL.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream);
                wr.write(reqParam);
                wr.flush();

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
                        validate = true
                    }
                }
            }
            println("Validation est : $validate")
            callback(validate)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}

