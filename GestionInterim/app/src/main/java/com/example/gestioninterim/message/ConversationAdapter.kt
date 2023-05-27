package com.example.gestioninterim.message

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.ListConversationDao

class ConversationAdapter(private val conversations: List<ListConversationDao> ,private val currentUser:String) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("participantId", conversation.participantId)
            intent.putExtra("userId", currentUser)
            intent.putExtra("participantName",conversation.userName)
            context.startActivity(intent)
        }
    }
    override fun getItemCount(): Int {
        return conversations.size
    }

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.textName)
        private val lastMessageTextView: TextView = itemView.findViewById(R.id.textMessage)
        private val dateTextView: TextView = itemView.findViewById(R.id.textDate)

        fun bind(conversation: ListConversationDao) {
            // Set the user name, last message, and date to the respective TextViews
            userNameTextView.text = conversation.userName
            lastMessageTextView.text = conversation.lastMessage
            dateTextView.text = conversation.date
            }
    }
}


