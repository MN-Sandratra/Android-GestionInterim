package com.example.gestioninterim.message

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gestioninterim.R
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.google.gson.Gson

class ConversationListActivity : AppCompatActivity() {
    private lateinit var user : UtilisateurInterimaire
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_list)
        val sharedPreferences = this.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurInterimaire::class.java)


        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // Crée une instance du ConversationListFragment
        val conversationListFragment = ConversationListFragment()
        val bundle = Bundle()
        bundle.putString(
            "myUser",
            ""+user._id,
        ) // Remplacez "valeur de ma variable" par votre propre valeur

        conversationListFragment.setArguments(bundle)

        // Remplace le contenu du conteneur avec le fragment
        fragmentTransaction.replace(R.id.fragmentContainer, conversationListFragment)
        fragmentTransaction.commit()
    }
}