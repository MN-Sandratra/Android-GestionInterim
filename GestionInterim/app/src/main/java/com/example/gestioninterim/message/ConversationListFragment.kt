package com.example.gestioninterim.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.ListConversationDao
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.resultEvent.ConversationsResultEvent
import com.example.gestioninterim.services.CandidatureResultService
import com.example.gestioninterim.services.DiscussionsService
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*

class ConversationListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var conversationAdapter: ConversationAdapter
    private val conversationList = mutableListOf<ListConversationDao>()
    private lateinit var user : String;


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        launchServiceDiscussion()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_conversation_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        user= getArguments()?.getString("myUser").toString();

        launchServiceDiscussion();

        return view
    }

    fun launchServiceDiscussion() {
        val intent = Intent(requireContext(), DiscussionsService::class.java)
        intent.putExtra("userId", user)
        requireContext().startService(intent)
    }

    fun onConversationClicked(participantId: String) {
        val intent = Intent(requireContext(), MessageActivity::class.java)
        intent.putExtra("participantId", participantId)
        startActivity(intent)
    }
    private fun formatTimestamp(timestamp: String): String {
        // Parse the timestamp string to a Date object
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(timestamp)

        // Format the date to your desired format
        val outputFormat = SimpleDateFormat("dd/MM/yyyy-HH'h'mm", Locale.getDefault())
        return outputFormat.format(date)
    }

    private fun limitMessageLength(message: String, limit: Int): String {
        // Check if the message length exceeds the limit
        if (message.length <= limit) {
            return message
        } else {
            // Truncate the message to the limit and add ellipsis
            val truncatedMessage = message.substring(0, limit) + "..."
            return truncatedMessage
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onConversationsResultEvent(event: ConversationsResultEvent) {
        conversationList.clear()

        val conversations = event.conversations
        for (conversation in conversations) {
            val participant = conversation.participant
            val lastMessage = conversation.lastMessage
            val timestamp = conversation.timestamp
            // Format the timestamp
            val formattedTimestamp = formatTimestamp(timestamp)

            // Limit the message to 30 words with ellipsis if it exceeds the limit
            val limitedMessage = limitMessageLength(lastMessage, 30)

            // Create a new ListConversationDao object and add it to the list
            val listConversation = ListConversationDao(participant.id,participant.name, limitedMessage, formattedTimestamp)
            conversationList.add(listConversation)
        }
        conversationAdapter = ConversationAdapter(conversationList,user)
        recyclerView.adapter = conversationAdapter
    }
}
