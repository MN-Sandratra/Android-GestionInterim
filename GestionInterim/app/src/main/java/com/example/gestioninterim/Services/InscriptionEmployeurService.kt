package com.example.gestioninterim.Services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.models.UtilisateurInterimaire
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

class InscriptionEmployeurService : Service() {

    var utilisateur : UtilisateurEmployeur? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        utilisateur = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra("utilisateur", UtilisateurEmployeur::class.java)
        } else {
            @Suppress("DEPRECATION") intent?.getSerializableExtra("utilisateur") as UtilisateurEmployeur
        }

        utilisateur?.let { sendPostRequest(it) }

        return START_STICKY
    }



    fun sendPostRequest(user: UtilisateurEmployeur) {
        Executors.newSingleThreadExecutor().execute {

            var reqParam : String? = ""

            // J'ajoute à la requete les parameters si ils sont pas null ou vide ("")
            user.let {
                it.javaClass.declaredFields.forEach {
                    it.isAccessible = true
                    val propName = it.name
                    val propValue = it.get(user)
                    println("$propName = $propValue")
                    if (propValue != null) {
                        if (!propValue.toString().isEmpty()) {
                            reqParam += URLEncoder.encode(propName,"UTF-8") + "=" + URLEncoder.encode(propValue.toString(), "UTF-8") + "&"
                        }
                    }
                }
            }

            // Je supprime le dernier charactère de la chaîne
            val reqParambis = reqParam!!.substring(0, reqParam.length - 1)
            println("La requete est : $reqParambis")

            val mURL = URL("http://192.168.1.23:8000/api/employers")

            with(mURL.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream);
                wr.write(reqParambis);
                wr.flush();

                println("URL : $url")
                println("Response Code : $responseCode")

                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    println("Response : $response")
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}

