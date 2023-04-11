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
import android.widget.TextView
import android.widget.Toast
import com.example.gestioninterim.login.LoginActivity
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class FragmentValidationInscription : Fragment() {


    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inscription_validation, container, false)

        val clickHere = view.findViewById<TextView>(R.id.clickHere)

        clickHere.setOnClickListener{
            Toast.makeText(requireContext(), "Nous venons de vous renvoyer un code", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}