package com.example.gestioninterim.utilisateurEmployeur;

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.gestioninterim.R
import com.example.gestioninterim.message.ConversationListFragment
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class MainEmployeurActivity : AppCompatActivity() {

    var fragmentManager: FragmentManager? = null
    private lateinit var user : UtilisateurEmployeur
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employeur)


        val fragment = FragmentAccueilEmployeur()
        loadFragment(fragment, R.string.sub_title_accueil)
        val sharedPreferences = this.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurEmployeur::class.java)


        val navigationView = findViewById<BottomNavigationView>(R.id.navigation_employeur)

        navigationView.setOnItemSelectedListener {
            when(it.itemId)
            {
                R.id.home_page -> {
                    val fragment = FragmentAccueilEmployeur()
                    loadFragment(fragment, R.string.sub_title_accueil)
                    return@setOnItemSelectedListener true
                }
                R.id.voir_message_page -> {
                    val fragment = ConversationListFragment()
                    val bundle = Bundle()
                    bundle.putString(
                        "myUser",
                        ""+user._id,
                    )
                    fragment.arguments = bundle
                    loadFragment(fragment, R.string.voirMessageSubTitle)
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
