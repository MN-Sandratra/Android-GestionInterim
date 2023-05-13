package com.example.gestioninterim.utilisateurEmployeur;

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.gestioninterim.R
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainEmployeurActivity : AppCompatActivity() {

    var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employeur)

//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_employeur)
//        for (i in 0 until bottomNavigationView.menu.size()) {
//            val item = bottomNavigationView.menu.getItem(i)
//            val itemView = bottomNavigationView.findViewById<View>(item.itemId) as BottomNavigationItemView
//            itemView.setShifting(false)
//        }


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
