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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class OfferEmployerAdapter(private val onClickListener: (Offer) -> Unit) :

    RecyclerView.Adapter<OfferEmployerAdapter.OfferViewHolder>() {

    private var offers: List<Offer> = listOf()

    inner class OfferViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val metierTextView: TextView = view.findViewById(R.id.offreMetier)
        val lieuTextView: TextView = view.findViewById(R.id.offreLieu)
        val dateTextView: TextView = view.findViewById(R.id.offreDate)
        val heureTextView: TextView = view.findViewById(R.id.offreHeure)
        val voirPlusTextView: TextView = view.findViewById(R.id.voirPlus)
        val readMoreImage: ImageView = view.findViewById(R.id.readMoreImage)

        fun bind(offer: Offer) {
            metierTextView.text = offer.intitule
            lieuTextView.text = offer.ville
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(offer.dateDebut)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            heureTextView.text = timeFormat.format(offer.dateDebut)

            itemView.setOnClickListener {
                onClickListener(offer)
            }
        }




    }

    fun updateOffers(newOffers: List<Offer>) {
        offers = newOffers
        notifyDataSetChanged()
    }

    fun getOffers(): List<Offer> {
        return offers
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vertical_offre_employer, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.bind(offers[position])
    }

    override fun getItemCount(): Int {
        return offers.size
    }

}
