package com.example.gestioninterim.inscription

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.login.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class FragmentEmployeurInscription : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inscription_employeur, container, false)
        val clickHere = view.findViewById<TextView>(R.id.clickHere)

        clickHere.setOnClickListener{
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

        }

        val buttonValidate = view.findViewById<MaterialButton>(R.id.buttonValidationInscription)
        val inputNom = view.findViewById<TextInputEditText>(R.id.inputTextNom)
        val inputMdp = view.findViewById<TextInputEditText>(R.id.inputTextPassword)
        val inputConfirmMdp = view.findViewById<TextInputEditText>(R.id.inputTextConfirmPassword)
        val inputNumeroEntite = view.findViewById<TextInputEditText>(R.id.inputTextNumeroEntite)
        val inputAdresse = view.findViewById<TextInputEditText>(R.id.inputTextAdresse)
        val inputMailContact1 = view.findViewById<TextInputEditText>(R.id.inputTextMailContact1)

        buttonValidate.setOnClickListener{

            val isMdpMatching = inputMdp.text.toString() == inputConfirmMdp.text.toString()

            val allInputsFilled = listOf(inputNom, inputNumeroEntite,inputAdresse, inputMailContact1 ,inputMdp, inputConfirmMdp).all {
                it.text?.isNotEmpty() == true
            } && isMdpMatching

            if (allInputsFilled) {
                val fragment = FragmentValidationInscription()
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction().replace(R.id.containerInscription, fragment).commit()
            } else if (!isMdpMatching) {
                Toast.makeText(requireContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireContext(), "Vous n'avez pas rempli les champs obligatoires", Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }
}