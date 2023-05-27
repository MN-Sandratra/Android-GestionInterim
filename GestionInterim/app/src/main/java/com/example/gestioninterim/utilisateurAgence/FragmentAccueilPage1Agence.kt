package com.example.gestioninterim.utilisateurEmployeur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.gestioninterim.R
import com.example.gestioninterim.utilisateurAgence.FragmentConsultAgence
import com.example.gestioninterim.utilisateurAgence.FragmentDeposerAnnoncesAgence
import com.example.gestioninterim.utilisateurAgence.FragmentProfilAgence
import com.example.gestioninterim.utilisateurAgence.MainAgenceActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout

class FragmentAccueilPage1Agence : Fragment() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_agence_accueil_page_1, container, false)

        val cardViewDeposerAnnonces = view.findViewById<MaterialCardView>(R.id.cardViewDeposerAnnonces)
        val cardViewConsult = view.findViewById<MaterialCardView>(R.id.cardViewConsult)
        val cardViewProfil = view.findViewById<MaterialCardView>(R.id.cardViewProfil)
        val cardViewMissionsCheck = view.findViewById<MaterialCardView>(R.id.cardViewMissionsCheck)
        val cardViewMissionsEnCours = view.findViewById<MaterialCardView>(R.id.cardViewMissionsEnCours)


        val parentActivity = activity as MainAgenceActivity
        val navigationView = parentActivity.findViewById<BottomNavigationView>(R.id.navigation_employeur)

        cardViewDeposerAnnonces.setOnClickListener{
            // Naviguez vers FragmentConsultEmployer
            val fragment = FragmentDeposerAnnoncesAgence()
            parentActivity.loadFragment(fragment, R.string.add_offers_title)
            navigationView.selectedItemId = R.id.deposer_annonces_page
        }

        cardViewConsult.setOnClickListener{
            val consultEmployerFragment = FragmentConsultAgence()
            parentActivity.loadFragment(consultEmployerFragment, R.string.sub_title_search)
            navigationView.selectedItemId = R.id.voir_annonces_page


        }

        cardViewProfil.setOnClickListener{
            val fragment = FragmentProfilAgence()
            parentActivity.loadFragment(fragment, R.string.sub_title_search)
            navigationView.selectedItemId = R.id.profil_page
        }

        cardViewMissionsCheck.setOnClickListener{

            val consultEmployerFragment = FragmentConsultAgence()
            parentActivity.loadFragment(consultEmployerFragment, R.string.sub_title_search)
            navigationView.selectedItemId = R.id.voir_annonces_page

        }

        cardViewMissionsEnCours.setOnClickListener{

            val consultEmployerFragment = FragmentConsultAgence()
            parentActivity.loadFragment(consultEmployerFragment, R.string.sub_title_search)
            navigationView.selectedItemId = R.id.voir_annonces_page

        }


        return view
    }
}