package com.example.gestioninterim.utilisateurInterimaire

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.login.LoginActivity
import com.example.gestioninterim.message.ConversationListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class FragmentAccueilInterimaire : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_interimaire_accueil, container, false)

        val cardViewAccueil = view.findViewById<MaterialCardView>(R.id.cardViewLogin)
        val cardViewSearch = view.findViewById<MaterialCardView>(R.id.cardViewSearch)
        val cardMessage=view.findViewById<MaterialCardView>(R.id.cardViewMessage);

        cardMessage.setOnClickListener {
            val intent = Intent(context, ConversationListActivity::class.java)
            startActivity(intent)
        }


//        val parentActivity = activity as MainInterimaireActivity
//        val navigationView = parentActivity.findViewById<BottomNavigationView>(R.id.navigation_anonyme)

//        cardViewAccueil.setOnClickListener{
//            val intent = Intent(requireContext(), LoginActivity::class.java)
//            startActivity(intent)
//            navigationView.selectedItemId = R.id.profil_page
//        }
//
//        cardViewSearch.setOnClickListener{
//            val fragment = FragmentSearchAnonyme()
//            parentActivity.loadFragment(fragment, R.string.sub_title_search)
//            navigationView.selectedItemId = R.id.search_page
//        }

        return view
    }
}