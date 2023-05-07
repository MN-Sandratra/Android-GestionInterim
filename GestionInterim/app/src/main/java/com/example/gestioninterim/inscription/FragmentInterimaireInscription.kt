package com.example.gestioninterim.inscription

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.services.InscriptionService
import com.example.gestioninterim.login.LoginActivity
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*

class FragmentInterimaireInscription : Fragment() {


    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inscription_interimaire, container, false)

        // Récupération du bouton pour selectionner le cv
        val getCv = view.findViewById<TextInputLayout>(R.id.inscriptionInterimaireCv)
        val nomCv = view.findViewById<TextInputEditText>(R.id.inputCv)
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
        val inputDateNaissance = view.findViewById<TextInputEditText>(R.id.inputDateNaissance)
        val inputVille = view.findViewById<TextInputEditText>(R.id.inputVille)
        val inputCommentaires = view.findViewById<TextInputEditText>(R.id.inputCommentaires)

        // Définition de l'URI du CV
        var selectedFileUri: Uri? = null

        // Adapter pour la sélection des pays
        val nationalities = resources.getStringArray(R.array.nationalities)
        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, nationalities)
        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.inputNationnalite)
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

        println("La date de naissance est : " + inputDateNaissance.text)


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

                val inputDate : String? = inputDateNaissance?.text?.toString()
                val inputMailText: String? = inputMail?.text?.toString()
                val inputTelephoneText: String? = inputTelephone?.text?.toString()
                val inputNationalite : String? = autoCompleteTextView?.text?.toString()
                val inputVilleText: String? = inputVille?.text?.toString()
                val inputCommentairesText: String? = inputCommentaires?.text?.toString()
                val inputPasswordEncrypt : String = hashPassword(inputMdp.text.toString())

                val date: String? = if (!inputDate?.isEmpty()!!) {
                    val originalDateFormat = SimpleDateFormat("dd/MM/yyyy")
                    val parsedDate = originalDateFormat.parse(inputDate.toString())
                    val newDateFormat = SimpleDateFormat("yyyy/MM/dd")
                    newDateFormat.format(parsedDate)
                } else {
                    ""
                }

                val user = UtilisateurInterimaire(
                    inputPrenom.text.toString(),
                    inputNom.text.toString(),
                    inputNationalite,
                    date,
                    inputTelephoneText,
                    inputMailText,
                    inputVilleText,
                    "",
                    inputCommentairesText,
                    inputPasswordEncrypt
                )

                launchServiceInscription(user)

                val fragment = FragmentValidationInscription()

                val args = Bundle()
                args.putString("email", inputMailText)

                fragment.arguments = args

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

    fun launchServiceInscription(user : UtilisateurInterimaire){
        val intent = Intent(requireContext(), InscriptionService::class.java)
        intent.putExtra("utilisateur", user as Serializable)
        intent.putExtra("type", "jobseekers")
        requireActivity().startService(intent)
    }

    fun hashPassword(password: String): String {
        try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val hash = messageDigest.digest(password.toByteArray(StandardCharsets.UTF_8))
            val hexString = StringBuilder()
            for (byte in hash) {
                val hex = String.format("%02X", byte)
                hexString.append(hex)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Error hashing password", e)
        }
    }
}
