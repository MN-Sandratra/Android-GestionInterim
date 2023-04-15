package com.example.gestioninterim.utilisateurEmployeur;

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.gestioninterim.R

class MainEmployeurActivity : AppCompatActivity() {

    var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interimaire)

        val fragment = FragmentAccueilEmployeur()
        loadFragment(fragment, R.string.sub_title_accueil)

//        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_anonyme)

//        navigationView.setOnItemSelectedListener {
//            when(it.itemId)
//            {
//                R.id.home_page -> {
//                    val fragment = FragmentAccueilAnonyme()
//                    loadFragment(fragment, R.string.sub_title_accueil)
//                    return@setOnItemSelectedListener true
//                }
//                R.id.search_page -> {
//                    val fragment = FragmentSearchAnonyme()
//                    loadFragment(fragment, R.string.sub_title_search)
//                    return@setOnItemSelectedListener true
//                }
//                R.id.profil_page -> {
//                    val intent = Intent(this, LoginActivity::class.java)
//                    startActivity(intent)
//                    return@setOnItemSelectedListener true
//                }
//                else -> false
//            }
//        }

    }

    fun loadFragment(fragment : Fragment, string: Int){

        findViewById<TextView>(R.id.subtitle).text = resources.getString(string)
        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.containerInterimaireFragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


}
