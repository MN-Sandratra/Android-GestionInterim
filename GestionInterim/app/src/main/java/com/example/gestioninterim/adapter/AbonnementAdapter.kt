package com.example.gestioninterim.adapter

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.models.Offer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.Abonnement
import com.google.android.material.button.MaterialButton
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AbonnementAdapter(private val abonnementClickListener: OnAbonnementClickListener) : RecyclerView.Adapter<AbonnementAdapter.AbonnementViewHolder>(){    private var abonnements: List<Abonnement> = listOf()

    inner class AbonnementViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val abonnementTypeTextView: TextView = view.findViewById(R.id.typeAbonnementText)
        val shortDescriptionTypeTextView: TextView = view.findViewById(R.id.ShortDescriptionAbonnementText)
        val prixTextView: TextView = view.findViewById(R.id.PrixAbonnementText)
        val abonnementButton: MaterialButton = view.findViewById(R.id.moreDetailsButton)

        fun bind(abonnement: Abonnement) {
            abonnementTypeTextView.text = abonnement.subscriptionName
            shortDescriptionTypeTextView.text = abonnement.shortDescription
            prixTextView.text = abonnement.price.toString() + "€"


            abonnementButton.setOnClickListener {
                abonnementClickListener.onAbonnementButtonClick(abonnement)
            }
        }

    }

    fun updateAbonnements(newAbonnements: List<Abonnement>) {
        abonnements = newAbonnements
        notifyDataSetChanged()
    }

    fun getAbonnements(): List<Abonnement> {
        return abonnements
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbonnementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vertical_abonnement, parent, false)
        return AbonnementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AbonnementViewHolder, position: Int) {
        holder.bind(abonnements[position])
    }

    override fun getItemCount(): Int {
        return abonnements.size
    }

}
