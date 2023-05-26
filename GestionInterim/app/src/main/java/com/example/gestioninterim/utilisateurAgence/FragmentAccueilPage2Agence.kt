package com.example.gestioninterim.utilisateurAgence

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.gestioninterim.R
import com.example.gestioninterim.login.LoginActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout

class FragmentAccueilPage2Agence : Fragment() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_employer_accueil_page_2, container, false)

        val cardViewDeconnexion = view.findViewById<MaterialCardView>(R.id.cardViewDeconnexion)

        cardViewDeconnexion.setOnClickListener {
            // Effacer les données sauvegardées
            val sharedPreferences = activity?.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Déconnexion")
            builder.setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
            builder.setPositiveButton("Oui") { dialog, _ ->

                sharedPreferences?.edit()?.clear()?.apply()
                // Démarrer LoginActivity
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                // Terminer l'activité actuelle
                activity?.finish()
                dialog.dismiss()
            }
            builder.setNegativeButton("Non") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }


        return view
    }
}