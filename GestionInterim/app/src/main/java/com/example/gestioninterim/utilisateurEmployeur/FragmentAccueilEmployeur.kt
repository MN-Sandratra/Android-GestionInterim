package com.example.gestioninterim.utilisateurEmployeur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.google.android.material.card.MaterialCardView

class FragmentAccueilEmployeur : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_interimaire_accueil, container, false)

        val cardViewAccueil = view.findViewById<MaterialCardView>(R.id.cardViewLogin)
        val cardViewSearch = view.findViewById<MaterialCardView>(R.id.cardViewSearch)


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