package com.example.gestioninterim.utilisateurInterimaire

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R

class FragmentCandidaturesRefusees : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_interimaire_candidatures_refusees, container, false)

        return view
    }
}