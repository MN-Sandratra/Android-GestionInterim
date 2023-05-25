package com.example.gestioninterim.utilisateurInterimaire;

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.gestioninterim.R
import com.example.gestioninterim.commonFragments.FragmentOfferAnonyme
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainInterimaireActivity : AppCompatActivity() {

    var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interimaire)

        val fragment = FragmentAccueilInterimaire()
        loadFragment(fragment, R.string.sub_title_accueil)

        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_interimaire)

        navigationView.setOnItemSelectedListener {
            when(it.itemId)
            {
                R.id.home_page -> {
                    val fragment = FragmentAccueilInterimaire()
                    loadFragment(fragment, R.string.sub_title_accueil)
                    return@setOnItemSelectedListener true
                }
                R.id.search_page -> {
                    val fragment = FragmentSearchInterimaire()
                    loadFragment(fragment, R.string.sub_title_search)
                    return@setOnItemSelectedListener true
                }
                R.id.candidatures_page -> {
                    val fragment = FragmentCandidaturesInterimaire()
                    loadFragment(fragment, R.string.sub_title_consult_missions)
                    return@setOnItemSelectedListener true
                }
                R.id.profil_page -> {
                    val fragment = FragmentProfilInterimaire()
                    loadFragment(fragment, R.string.profilUtilisateurTitle)
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }

    }

    fun loadFragment(fragment: Fragment, string: Int){

        findViewById<TextView>(R.id.subtitle).text = resources.getString(string)
        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.containerInterimaireFragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}
