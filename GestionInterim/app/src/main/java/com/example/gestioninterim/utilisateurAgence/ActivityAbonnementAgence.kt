package com.example.gestioninterim.utilisateurAgence

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.gestioninterim.R
import com.example.gestioninterim.adapter.OnAbonnementClickListener
import com.example.gestioninterim.models.Abonnement

class ActivityAbonnementAgence : AppCompatActivity(), OnAbonnementClickListener {

    var fragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abonnement)

        val fragment = FragmentAbonnementConsultationAgence()
        loadFragment(fragment, R.string.abonnementConsult)

    }

    fun loadFragment(fragment : Fragment, string: Int){

        findViewById<TextView>(R.id.subtitleAbonnement).text = resources.getString(string)

        fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.containerAbonnementFragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onAbonnementButtonClick(abonnement: Abonnement) {
        val fragment = FragmentAbonnementChoixAgence(
            abonnement.price,
            abonnement.subscriptionName,
            abonnement.description,
            abonnement.cancellationConditions
        )

        loadFragment(fragment, R.string.abonnementChoix)
    }

}