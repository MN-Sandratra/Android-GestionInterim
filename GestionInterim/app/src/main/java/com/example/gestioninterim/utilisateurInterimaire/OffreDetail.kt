package com.example.gestioninterim.utilisateurInterimaire

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.OfferResult
import com.example.gestioninterim.utilisateurInterimaire.FragmentOfferDetails.Companion.ARG_OFFER
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class OffreDetail : Fragment() {

    private lateinit var offer: OfferResult

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_offre_detail_offre, container, false)

        val gson = Gson()
        val offerJson = arguments?.getString(ARG_OFFER)
        offer = gson.fromJson(offerJson, OfferResult::class.java)

        Log.d("Affichage details", "===> $offer")

        val employeurTextView = view.findViewById<TextView>(R.id.employeurTextView)
        val intituleTextView = view.findViewById<TextView>(R.id.intituleTextView)
        val villeTextView = view.findViewById<TextView>(R.id.villeTextView)
        val adresseTextView = view.findViewById<TextView>(R.id.adresseTextView)
        val dateDebutTextView = view.findViewById<TextView>(R.id.dateDebutTextView)
        val dateFinTextView = view.findViewById<TextView>(R.id.dateFinTextView)
        val experienceTextView = view.findViewById<TextView>(R.id.experienceTextView)
        val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)
        val tauxHoraireTextView = view.findViewById<TextView>(R.id.tauxHoraireTextView)
        val disponibiliteTextView = view.findViewById<TextView>(R.id.disponibiliteTextView)
        val etatTextView = view.findViewById<TextView>(R.id.etatTextView)

        employeurTextView.text = offer.employeur
        intituleTextView.text = offer.intitule
        villeTextView.text = offer.ville
        adresseTextView.text = offer.adressePostale
        dateDebutTextView.text = offer.dateDebut.toString()
        dateFinTextView.text = offer.dateFin.toString()
        experienceTextView.text = offer.experienceRequise
        descriptionTextView.text = offer.description
        tauxHoraireTextView.text = offer.tauxHoraire.toString()
        disponibiliteTextView.text = offer.disponibilite.toString()
        etatTextView.text = offer.etat

        val parentActivity = activity as MainInterimaireActivity
        val navigationView = parentActivity.findViewById<BottomNavigationView>(R.id.navigation_interimaire)

        return view
    }

    companion object {
        fun newInstance(offer: OfferResult): OffreDetail {
            val fragment = OffreDetail()
            val args = Bundle()
            args.putSerializable("offer", offer)
            fragment.arguments = args
            return fragment
        }
    }
}
