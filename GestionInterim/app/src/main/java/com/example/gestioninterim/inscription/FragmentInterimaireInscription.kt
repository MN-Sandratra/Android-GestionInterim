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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class FragmentInterimaireInscription : Fragment() {


    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inscription_interimaire, container, false)

        // Récupération du bouton pour selectionner le cv
        val getCv = view.findViewById<TextInputLayout>(R.id.inscriptionInterimaireCv)
        val nomCv = view.findViewById<TextInputEditText>(R.id.nomCvInterimaire)
        val clickHere = view.findViewById<TextView>(R.id.clickHere)

        // Définition du bouton de validation
        val buttonValidate = view.findViewById<MaterialButton>(R.id.buttonValidationInscription)

        // Définition des inputs obligatoires
        val inputNom = view.findViewById<TextInputEditText>(R.id.inputTextNom)
        val inputPrenom = view.findViewById<TextInputEditText>(R.id.inputTextPrenom)
        val inputMdp = view.findViewById<TextInputEditText>(R.id.inputTextPassword)
        val inputConfirmMdp = view.findViewById<TextInputEditText>(R.id.inputTextConfirmPassword)
        val inputMail = view.findViewById<TextInputEditText>(R.id.inputTextMail)
        val inputTelephone = view.findViewById<TextInputEditText>(R.id.inputTextTelephone)

        // Définition de l'URI du CV
        var selectedFileUri: Uri? = null

        // Adapter pour la sélection des pays
        val nationalities = resources.getStringArray(R.array.nationalities)
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, nationalities)
        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.nationality_dropdown)
        autoCompleteTextView.setAdapter(adapter)

        // Explorateur de fichier
        val myLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                selectedFileUri = result.data?.data
                val contentResolver = requireContext().contentResolver

                // Récupérer le nom du fichier à partir de l'URI
                val cursor = contentResolver.query(selectedFileUri!!, null, null, null, null)
                cursor?.moveToFirst()

                val displayName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))

                // Je modifie le nom du fichier courant
                nomCv.setText("$displayName")
                getCv.setEndIconDrawable(R.drawable.ic_delete)
                cursor?.close()
            }
        }

        // Je vais demander la permission
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                isGranted: Boolean ->

            // Si permission acceptée
            if (isGranted) {
                // Je lance l'explorateur de fichier
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/pdf"
                myLauncher.launch(intent)
            } else {
            }
        }

        // Listener du bouton CV
        getCv.setEndIconOnClickListener {

            // Si aucun cv n'est selectionné
            if (nomCv.text.toString().isEmpty()) {
                when {
                    // Si la permission a déjà été accepté
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // Je lance l'explorateur de fichier
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "application/pdf"
                        myLauncher.launch(intent)
                    }
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    }
                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
                // Si un cv est déjà selectionné
            } else {
                nomCv.setText("")
                getCv.setEndIconDrawable(R.drawable.ic_import)
                Toast.makeText(
                    requireContext(),
                    "Vous venez de supprimer votre CV",
                    Toast.LENGTH_SHORT
                ).show()
                }
            }

        // Si j'ai déjà un compte, je vais me connecter
        clickHere.setOnClickListener{
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

        }

        // Listener du bouton de validation
        buttonValidate.setOnClickListener{

            // Vérification de correspondance entre les deux mots de passe
            val isMdpMatching = inputMdp.text.toString() == inputConfirmMdp.text.toString()

            // Vérification du remplissage de l'email ou du téléphone
            val isEmailOrTelephoneFilled = inputMail.text?.isNotEmpty() == true || inputTelephone.text?.isNotEmpty() == true

            // Vérification que chaque input soit remplis
            val allInputsFilled = listOf(inputNom, inputPrenom, inputMdp, inputConfirmMdp).all {
                it.text?.isNotEmpty() == true
            } && isMdpMatching && isEmailOrTelephoneFilled

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