package com.example.gestioninterim.message

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.Message

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(message: Message) {
        // Bind the message data to the views in the view holder
        val messageTextView: TextView = itemView.findViewById(R.id.textViewMessage)
        messageTextView.text = message.content
    }
}
