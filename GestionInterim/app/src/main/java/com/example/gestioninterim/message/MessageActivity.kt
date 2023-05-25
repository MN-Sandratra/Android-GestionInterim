package com.example.gestioninterim.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.Message

class MessageActivity : AppCompatActivity() {

    private val messageList = mutableListOf<Message>()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        // Initialize RecyclerView and adapter
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val messageEditText:EditText=findViewById(R.id.editTextMessage)
        // Set up Send button click listener
        val sendButton: Button = findViewById(R.id.buttonSend)
        sendButton.setOnClickListener {
            val messageText: String = messageEditText.text.toString()
            sendMessage(messageText)
            messageEditText.text.clear()
        }
    }

    private fun sendMessage(messageText: String) {
        // Implement the logic to send the message to the receiver
        // You can use the MessageService or any other communication mechanism here
        // After sending the message, add it to the messageList and notify the adapter
        val senderId = "your_sender_id"
        val receiverId = "your_receiver_id"
        val message = Message(senderId, receiverId, messageText, ""+System.currentTimeMillis())
        messageList.add(message)
        messageAdapter.notifyItemInserted(messageList.size - 1)

        // You may also want to scroll the RecyclerView to the bottom to show the latest message
        // recyclerView.scrollToPosition(messageList.size - 1)
    }
}
