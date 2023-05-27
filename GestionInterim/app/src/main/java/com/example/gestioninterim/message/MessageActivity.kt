package com.example.gestioninterim.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.Message
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.resultEvent.CandidaturesResultEvent
import com.example.gestioninterim.resultEvent.MessageGetResultEvent
import com.example.gestioninterim.services.MessageService
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MessageActivity : AppCompatActivity() {

    private var messageList = mutableListOf<Message>()
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var user : UtilisateurInterimaire
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val sharedPreferences = this?.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurInterimaire::class.java)

        // Initialize RecyclerView and adapter
        recyclerView= findViewById(R.id.recyclerView)
        messageAdapter = MessageAdapter(messageList,user._id)
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

            val toolbar: Toolbar = findViewById(R.id.toolbar)
            val name=intent.getStringExtra("participantName")
            toolbar.setTitle(name)
            toolbar.setTitleTextColor(Color.WHITE)

        val messageEditText:EditText=findViewById(R.id.editTextMessage)
        val sendButton: Button = findViewById(R.id.buttonSend)


        getAllMessages()
        sendButton.setOnClickListener {
            val messageText: String = messageEditText.text.toString()
            sendMessage(messageText)
            messageEditText.text.clear()
        }
    }
    override fun onResume() {
        super.onResume()
        getAllMessages()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetMessageResult(event: MessageGetResultEvent) {
        messageList.clear()
        event.messages?.let { messageList.addAll(it) }
        messageAdapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(messageList.size - 1)
        Log.d("MESSAGE", "list => ${messageList[0].content}")
        Log.d("Affichage", "=> A TRAITER 2")
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun getAllMessages() {
        val senderId = user._id
        val receiverId = intent.getStringExtra("participantId")
        // Appelez le service de messages pour récupérer tous les messages correspondants
        val intent = Intent(applicationContext, MessageService::class.java)
        intent.putExtra("action", "getAllMessages")
        intent.putExtra("senderId", senderId)
        intent.putExtra("receiverId", receiverId)
        startService(intent)
    }

    private fun sendMessage(messageText: String) {
       if(messageText.trim().isNotEmpty()) {
            val senderId = user._id
            val receiverId = intent.getStringExtra("participantId")
            val message = receiverId?.let {
                Message(
                    senderId,
                    it, messageText, "" + System.currentTimeMillis()
                )
            }

            val intent = Intent(applicationContext, MessageService::class.java)
            intent.putExtra("messageRequest", message)
            intent.putExtra("action","sendMessage")
            startService(intent)

            if (message != null) {
                messageList.add(message)
            }
            messageAdapter.notifyItemInserted(messageList.size - 1)
        }
        // You may also want to scroll the RecyclerView to the bottom to show the latest message
        recyclerView.scrollToPosition(messageList.size - 1)
    }
}
