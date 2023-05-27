package com.example.gestioninterim.services

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Message
import com.example.gestioninterim.resultEvent.MessageGetResultEvent
import com.example.gestioninterim.resultEvent.MessageResultEvent
import com.example.gestioninterim.resultEvent.OffersResultEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.greenrobot.eventbus.EventBus
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors


class MessageService: Service() {
    lateinit var messageRequest:Message;
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Implement your logic here for processing the message and sending it

        val action = intent?.getStringExtra("action")
        if (action == "sendMessage") {

            messageRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent!!.getSerializableExtra("messageRequest", Message::class.java ) as Message
        } else {
            @Suppress("DEPRECATION") intent?.getSerializableExtra("messageRequest") as Message
        }
        sendPostRequestMessage(messageRequest) { messa ->
            val event = MessageResultEvent(messa)
            EventBus.getDefault().post(event)
        }}else if (action == "getAllMessages") {
            val senderId = intent?.getStringExtra("senderId")
            val receiverId = intent?.getStringExtra("receiverId")
            sendGetRequestMessages(senderId,receiverId) { messa ->
                println("condaka getAllMessages")
                val message = MessageGetResultEvent(messa)
                EventBus.getDefault().post(message)
            }
        }
        return START_STICKY
    }

    fun sendPostRequestMessage(message : Message, callback: (success: Boolean) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            val gson = Gson()
            val offerJson = gson.toJson(message)

            val jsonMediaType = "application/json; charset=utf-8".toMediaType()
            val body = offerJson.toRequestBody(jsonMediaType)
            println("alefako ty an")
            val request = Request.Builder()
                .url("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/messages/")
                .post(body)
                .build()

            val client = OkHttpClient()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    // Successfully created
                    callback(true)
                } else {
                    // Failed to create
                    val inputLine = response.body?.string()
                    println("Response error body : $inputLine")
                    callback(false)
                }
            }
        }
    }
    fun sendGetRequestMessages(
        senderId: String?,
        receiverId: String?,
        callback: (offers: List<Message>) -> Unit
    ) {
        println("gety messazy")
        Executors.newSingleThreadExecutor().execute {
            // Je définis les paramètres de la requête
            val reqParamBuilder = StringBuilder()

            val mURL = URL("http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/messages/${senderId}/${receiverId}")
            with(mURL.openConnection() as HttpURLConnection) {
                // optional default is GET
                requestMethod = "GET"

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
                        // Convert JSON response to a list of Offer objects
                        val gson = Gson()
                        val messageType = object : TypeToken<List<Message>>() {}.type
                        val messages: List<Message> = gson.fromJson(response.toString(), messageType)
                        callback(messages)
                    }
                } else {
                    callback(emptyList())
                }
            }
        }
    }
}