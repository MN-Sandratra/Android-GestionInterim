package com.example.gestioninterim.slides

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R

class FragmentFirstSlides : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view : View = inflater.inflate(R.layout.fragment_slide_first_slides, container, false)

        val bundle : Bundle? = arguments;

        val title : TextView = view.findViewById(R.id.titleFirstSlide)
        val text : TextView = view.findViewById(R.id.textFirstSlide)

        title.text = bundle!!.getString("title")
        text.text = bundle!!.getString("text")

        return view
    }
}