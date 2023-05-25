package com.example.gestioninterim.services

import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.models.Conversation

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.gestioninterim.models.Participant
import com.example.gestioninterim.resultEvent.ConversationsResultEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.lang.reflect.Type
import java.net.URLEncoder
import java.util.concurrent.Executors

class DiscussionsService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val userId = intent?.getStringExtra("userId")

        sendGetRequestConversations(userId) { conversations ->
            val event = ConversationsResultEvent(conversations)
            EventBus.getDefault().post(event)
            Log.d("CONVERSATIONS", "affichage => $conversations")
        }

        return START_STICKY
    }

    private fun sendGetRequestConversations(userId: String?, callback: (conversations: List<Conversation>) -> Unit) {
        Executors.newSingleThreadExecutor().execute {

            // Build the request URL with query parameter
            val url = "http://${BuildConfig.ADRESSE_IP}:${BuildConfig.PORT}/api/messages/${URLEncoder.encode(userId, "UTF-8")}"

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        response.body?.let {
                            val gson = Gson()
                            val messageType: Type = object : TypeToken<List<Conversation>>() {}.type
                            val conversations: List<Conversation> = gson.fromJson(it.string(), messageType)
                            callback(conversations)
                        }
                    } else {
                        callback(emptyList())
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback(emptyList())
                }
            })
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
