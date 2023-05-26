package com.example.gestioninterim.utilisateurEmployeur;

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.gestioninterim.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainEmployeurActivity : AppCompatActivity() {

    var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employeur)


        val fragment = FragmentAccueilEmployeur()
        loadFragment(fragment, R.string.sub_title_accueil)

        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_employeur)

        navigationView.setOnItemSelectedListener {
            when(it.itemId)
            {
                R.id.home_page -> {
                    val fragment = FragmentAccueilEmployeur()
                    loadFragment(fragment, R.string.sub_title_accueil)
                    return@setOnItemSelectedListener true
                }
                R.id.voir_annonces_page -> {
                    val fragment = FragmentConsultEmployer()
                    loadFragment(fragment, R.string.voirAnnoncesSubTitle)
                    return@setOnItemSelectedListener true
                }
                R.id.deposer_annonces_page -> {
                    val fragment = FragmentDeposerAnnoncesEmployeur()
                    loadFragment(fragment, R.string.add_offers_title)
                    return@setOnItemSelectedListener true
                }
                R.id.profil_page -> {
                    val fragment = FragmentProfilEmployeur()
                    loadFragment(fragment, R.string.profilUtilisateurTitle)
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }

    }

    fun loadFragment(fragment : Fragment, string: Int){

        findViewById<TextView>(R.id.subtitle).text = resources.getString(string)
        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.containerEmployeurFragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}
