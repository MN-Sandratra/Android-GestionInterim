package com.example.gestioninterim.utilisateurAgence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R

class FragmentSlidesAgence : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view : View = inflater.inflate(R.layout.fragment_slide_employeur, container, false)

        val bundle : Bundle? = arguments;

        val title : TextView = view.findViewById(R.id.titleSlideEmployer)
        val text : TextView = view.findViewById(R.id.textSlideEmployer)

        title.text = bundle!!.getString("title")
        text.text = bundle!!.getString("text")

        return view
    }
}
