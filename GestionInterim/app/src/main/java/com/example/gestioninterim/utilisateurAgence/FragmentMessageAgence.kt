package com.example.gestioninterim.utilisateurAgence

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R

class FragmentMessageAgence : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // CHANGER LE LAYOUT
        val view : View = inflater.inflate(R.layout.fragment_slide_employeur, container, false)



        return view
    }
}
