package com.example.gestioninterim.utilisateurEmployeur

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.google.android.material.card.MaterialCardView

class FragmentMissionsValideesEmployer : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_menu_top_missions_validees, container, false)

        return view
    }
}