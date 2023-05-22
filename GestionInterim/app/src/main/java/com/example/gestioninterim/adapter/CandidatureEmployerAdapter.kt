package com.example.gestioninterim.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.CandidatureEmployerResult
import java.text.SimpleDateFormat
import java.util.*

class CandidatureEmployerResultAdapter(private val onClickListener: (CandidatureEmployerResult) -> Unit) :
    RecyclerView.Adapter<CandidatureEmployerResultAdapter.CandidatureEmployerResultViewHolder>() {

    private var candidatures: List<CandidatureEmployerResult> = listOf()

    inner class CandidatureEmployerResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val metierTextView: TextView = view.findViewById(R.id.offreMetier)
        val lieuTextView: TextView = view.findViewById(R.id.offreLieu)
        val dateTextView: TextView = view.findViewById(R.id.offreDate)
        val heureTextView: TextView = view.findViewById(R.id.offreHeure)
        val voirPlusTextView: TextView = view.findViewById(R.id.voirPlus)
        val readMoreImage: ImageView = view.findViewById(R.id.readMoreImage)

        fun bind(candidatureResult: CandidatureEmployerResult) {
            metierTextView.text = candidatureResult.offre.intitule
            lieuTextView.text = candidatureResult.offre.ville
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(candidatureResult.offre.dateDebut)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            heureTextView.text = timeFormat.format(candidatureResult.offre.dateDebut)

            itemView.setOnClickListener {
                onClickListener(candidatureResult)
            }
        }
    }


    fun updateCandidatures(newCandidatures: List<CandidatureEmployerResult>) {
        candidatures = newCandidatures
        notifyDataSetChanged()
    }

    fun getCandidatures(): List<CandidatureEmployerResult> {
        return candidatures
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidatureEmployerResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vertical_candidature_employer, parent, false)
        return CandidatureEmployerResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandidatureEmployerResultViewHolder, position: Int) {
        holder.bind(candidatures[position])
    }

    override fun getItemCount(): Int {
        return candidatures.size
    }

}
