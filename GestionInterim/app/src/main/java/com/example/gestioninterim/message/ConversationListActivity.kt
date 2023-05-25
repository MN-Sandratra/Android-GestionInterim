package com.example.gestioninterim.message

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gestioninterim.R

class ConversationListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_list)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Crée une instance du ConversationListFragment
        val conversationListFragment = ConversationListFragment()

        // Remplace le contenu du conteneur avec le fragment
        fragmentTransaction.replace(R.id.fragmentContainer, conversationListFragment)
        fragmentTransaction.commit()
    }
}