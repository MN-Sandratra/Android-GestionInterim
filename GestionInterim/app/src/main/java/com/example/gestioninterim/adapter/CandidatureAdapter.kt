package com.example.gestioninterim.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.gestioninterim.R
import com.example.gestioninterim.models.Candidature
import com.example.gestioninterim.models.CandidatureEmployerResult
import java.text.SimpleDateFormat
import java.util.*

class CandidatureAdapter(context: Context, candidatures: List<Candidature>)
    : ArrayAdapter<Candidature>(context, 0, candidatures) {

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.candidature_list_item, parent, false)

        val candidature = getItem(position)

        val nameTextView = view.findViewById<TextView>(R.id.nameTextView)
        val nationalityTextView = view.findViewById<TextView>(R.id.nationalityTextView)
        val dateOfBirthTextView = view.findViewById<TextView>(R.id.dateOfBirthTextView)
        val cvTextView = view.findViewById<TextView>(R.id.cvTextView)
        val lmTextView = view.findViewById<TextView>(R.id.lmTextView)

        nameTextView.text = "${candidature?.firstName} ${candidature?.lastName}"
        nationalityTextView.text = candidature?.nationality
        // Here is where we convert and reformat the date
        val date = inputFormat.parse(candidature?.dateOfBirth)
        dateOfBirthTextView.text = outputFormat.format(date)
        cvTextView.text = candidature?.cv
        lmTextView.text = candidature?.lm

        return view
    }
}
