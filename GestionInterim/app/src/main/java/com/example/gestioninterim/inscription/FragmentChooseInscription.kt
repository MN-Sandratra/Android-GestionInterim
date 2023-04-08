package com.example.gestioninterim.inscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.google.android.material.button.MaterialButton

class FragmentChooseInscription : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_choose_inscription, container, false)


        val checkBoxInterim = view.findViewById<CheckBox>(R.id.checkInterimaire)
        val checkBoxEmployeur = view.findViewById<CheckBox>(R.id.checkEmployeur)
        val checkBoxAgence = view.findViewById<CheckBox>(R.id.checkAgence)
        val buttonValidation = view.findViewById<MaterialButton>(R.id.materialButtonValidation)

        checkBoxInterim.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxEmployeur.isChecked = false
                checkBoxAgence.isChecked = false


            }
        }

        checkBoxEmployeur.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxInterim.isChecked = false
                checkBoxAgence.isChecked = false
            }
        }

        checkBoxAgence.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkBoxInterim.isChecked = false
                checkBoxEmployeur.isChecked = false
            }
        }

        lateinit var fragment: Fragment

        buttonValidation.setOnClickListener{
            fragment = if (checkBoxInterim.isChecked) {
                FragmentInterimaireInscription()
            } else if (checkBoxEmployeur.isChecked) {
                FragmentEmployeurInscription()
            } else if (checkBoxAgence.isChecked) {
                FragmentAgenceInscription()
            } else {
                Toast.makeText(requireContext(), "Veuillez cocher une case", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.containerChooseInscription, fragment).commit()
        }

        return view
    }
}