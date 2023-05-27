package com.example.gestioninterim.utilisateurAgence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.utilisateurEmployeur.FragmentAbonnementPaiement
import com.google.android.material.button.MaterialButton

class FragmentAbonnementChoixAgence(
    private val prixAbonnement: Int,
    private val typeAbonnement: String,
    private val description: String,
    private val conditionsDesabonnement: String
) : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_choix_abonnement, container, false)

        val prixAbonnementTextView : TextView = view.findViewById(R.id.PrixAbonnementText)
        val typeAbonnementTextView : TextView = view.findViewById(R.id.typeAbonnementText)
//        val conditionsDesabonnementTextView : TextView = view.findViewById(R.id.conditionDesabonnement)
        val descriptionTextView : TextView = view.findViewById(R.id.descriptionAbonnement)
        val validationAbonnementBouton : MaterialButton = view.findViewById(R.id.boutonValidationAbonnement)

        prixAbonnementTextView.text = prixAbonnement.toString() + "€"
        typeAbonnementTextView.text = typeAbonnement
        descriptionTextView.text = description
//        conditionsDesabonnementTextView.text = "- $conditionsDesabonnement"

        validationAbonnementBouton.setOnClickListener {
            val fragmentAbonnementPaiement = FragmentAbonnementPaiementAgence(prixAbonnement, typeAbonnement)

            parentFragmentManager
                .beginTransaction()
                .replace(R.id.containerAbonnementFragment, fragmentAbonnementPaiement)
                .addToBackStack(null)
                .commit()
        }

        return view

    }

}