package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.resultEvent.LoginResultEvent
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

class LoginService : Service(){

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val email = intent?.getStringExtra("email")
        val password = intent?.getStringExtra("password")


        sendPostRequestLogin(email.toString(), password.toString()) { message, type, nom, hasSubscription ->
            val event = LoginResultEvent(message, type, nom, hasSubscription)
            EventBus.getDefault().post(event)
        }

        return START_STICKY
    }



    fun sendPostRequestLogin(email: String, password: String, callback: (message: String?, type: String?, nom : String?, hasSubscription: Boolean?) -> Unit) {

        Executors.newSingleThreadExecutor().execute {

            // Je defini le corps de la requete
            var reqParam = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
            reqParam += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")

            val mURL = URL("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/login/")
            println("La requete est : $reqParam")

            with(mURL.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream);
                wr.write(reqParam);
                wr.flush();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader(InputStreamReader(inputStream)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        val jsonObject = JSONObject(response.toString())
                        val message = jsonObject.getString("message")
                        val type = jsonObject.getString("type")
                        val nom = jsonObject.getString("nom")
                        val hasSubscription = jsonObject.getBoolean("hasSubscription")
                        callback(message, type, nom, hasSubscription)
                    }
                }
                else {
                    callback(null, null, null, null)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}


