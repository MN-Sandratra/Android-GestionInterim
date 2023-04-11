package com.example.gestioninterim.inscription

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.google.android.material.textfield.TextInputLayout
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import com.example.gestioninterim.login.LoginActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class FragmentValidationInscription : Fragment() {

    private lateinit var inputFields: Array<TextInputEditText>

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inscription_validation, container, false)

        // Définition du bouton de validation
        val clickHere = view.findViewById<TextView>(R.id.clickHere)

        // Définition des chiffres
        val number1 = view.findViewById<TextInputEditText>(R.id.inputValidationNumber1)
        val number2 = view.findViewById<TextInputEditText>(R.id.inputValidationNumber2)
        val number3 = view.findViewById<TextInputEditText>(R.id.inputValidationNumber3)
        val number4 = view.findViewById<TextInputEditText>(R.id.inputValidationNumber4)

        inputFields = arrayOf(
            number1,
            number2,
            number3,
            number4
        )

        // Lorsqu'un chiffre est rempli, on focus sur celui d'après
        for (i in inputFields.indices) {
            val inputField = inputFields[i]
            inputField.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s != null && s.length == 1 && i < inputFields.size - 1) {
                        inputFields[i + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        // Listener sur le bouton de validation
        clickHere.setOnClickListener{
            Toast.makeText(requireContext(), "Nous venons de vous renvoyer un code", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}