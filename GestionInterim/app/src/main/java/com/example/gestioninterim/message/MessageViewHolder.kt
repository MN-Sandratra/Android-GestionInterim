package com.example.gestioninterim.message

import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.Message

class MessageViewHolder(itemView: View, private val currentUserId: String) : RecyclerView.ViewHolder(itemView) {

    fun bind(message: Message) {
        // Bind the message data to the views in the view holder
        val messageTextView: TextView = itemView.findViewById(R.id.textViewMessage)
        val layoutParams = messageTextView.layoutParams as RelativeLayout.LayoutParams
        messageTextView.text = message.content

        if (message.sender == currentUserId) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
            messageTextView.gravity = Gravity.END
            messageTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
            messageTextView.setBackgroundResource(R.drawable.message_bubble)
        } else {
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END)
            messageTextView.gravity = Gravity.START
            messageTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
            messageTextView.setBackgroundResource(R.drawable.message_bubble_other)
        }
    }
}
