package com.example.gestioninterim.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R

class ConversationAdapter:RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    private val conversations: List<Conversation> = mutableListOf() // Replace with your conversation data

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.textViewUserName)
        private val lastMessageTextView: TextView = itemView.findViewById(R.id.textViewLastMessage)
        private val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)

        fun bind(conversation: Conversation) {
            // Set the user name, last message, and date to the respective TextViews
            userNameTextView.text = conversation.userName
            lastMessageTextView.text = conversation.lastMessage
            dateTextView.text = conversation.date
        }
    }
}

data class Conversation(val userName: String, val lastMessage: String, val date:String)