package com.example.gestioninterim.utilisateurInterimaire

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.gestioninterim.R
import com.example.gestioninterim.inscription.FragmentValidationInscription
import com.example.gestioninterim.models.CandidatureToSend
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.resultEvent.AbonnementsResultEvent
import com.example.gestioninterim.resultEvent.ValidationBooleanEvent
import com.example.gestioninterim.services.CandidatureService
import com.example.gestioninterim.utilisateurEmployeur.MainEmployeurActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable
import java.text.SimpleDateFormat

class CandidatureFormFragment : Fragment() {

    private var lmFilePath: String? = null
    private var cvFilePath: String? = null
    private lateinit var user : UtilisateurInterimaire


    // Explorateur de fichier
    private lateinit var myLauncher: ActivityResultLauncher<Intent>

    // Je vais demander la permission
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_candidature_form, container, false)

        // Récupération du bouton pour selectionner le cv
        val getCv = view.findViewById<TextInputLayout>(R.id.inscriptionInterimaireCv)
        val nomCv = view.findViewById<TextInputEditText>(R.id.inputCv)

        val getLm = view.findViewById<TextInputLayout>(R.id.inscriptionInterimaireLm)
        val nomLm = view.findViewById<TextInputEditText>(R.id.inputLm)

        // Définition du bouton de validation
        val buttonValidate = view.findViewById<MaterialButton>(R.id.buttonValidationInscription)

        // Définition des inputs obligatoires
        val inputNom = view.findViewById<TextInputEditText>(R.id.inputTextNom)
        val inputPrenom = view.findViewById<TextInputEditText>(R.id.inputTextPrenom)
        val inputDateNaissance = view.findViewById<TextInputEditText>(R.id.inputDateNaissance)



        val sharedPreferences = activity?.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurInterimaire::class.java)

        Log.d("Affichage", "====> ${user.email}")

        // Définition de l'URI du CV
        var selectedFileUri: Uri? = null
        var selectedFileUriLm: Uri? = null

        // Adapter pour la sélection des pays
        val nationalities = resources.getStringArray(R.array.nationalities)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, nationalities)
        val autoCompleteTextView = view.findViewById<AutoCompleteTextView>(R.id.inputNationnalite)
        autoCompleteTextView.setAdapter(adapter)

        inputNom.setText(user.lastName)
        inputPrenom.setText(user.firstName)
        inputDateNaissance.setText(user.dateOfBirth)


        // Explorateur de fichier
        val myCvLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Récupération du Uri du fichier sélectionné pour le CV
                val uri: Uri? = result.data?.data
                // Utilisation du Uri (ex: affichage du nom du fichier dans un TextView)
                uri?.let {
                    val cursor: Cursor? = activity?.contentResolver?.query(it, null, null, null, null, null)
                    cursor?.use { c ->
                        val nameIndex: Int = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (c.moveToFirst()) {
                            val name: String = c.getString(nameIndex)
                            cvFilePath = name.replace(" ", "_")
                            nomCv.setText(name)
                            getCv.setEndIconDrawable(R.drawable.ic_delete)
                        }
                    }
                }
            }
        }

// Explorateur de fichier pour la lettre de motivation (lm)
        val myLmLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Récupération du Uri du fichier sélectionné pour la lettre de motivation (lm)
                val uri: Uri? = result.data?.data
                // Utilisation du Uri (ex: affichage du nom du fichier dans un TextView)
                uri?.let {
                    val cursor: Cursor? = activity?.contentResolver?.query(it, null, null, null, null, null)
                    cursor?.use { c ->
                        val nameIndex: Int = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (c.moveToFirst()) {
                            val name: String = c.getString(nameIndex)
                            lmFilePath = name.replace(" ", "_")
                            nomLm.setText(name)
                            getLm.setEndIconDrawable(R.drawable.ic_delete)
                        }
                    }
                }
            }
        }

        // Je vais demander la permission
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
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

        getCv.setEndIconOnClickListener {
            // Si aucun CV n'est sélectionné
            println("Click sur cv")
            if (nomCv.text.toString().isEmpty()) {
                // Je lance l'explorateur de fichiers pour sélectionner le CV
                selectFile(myCvLauncher)
            } else {
                nomCv.setText("")
                getCv.setEndIconDrawable(R.drawable.ic_import)
                Toast.makeText(requireContext(), "Vous venez de supprimer votre CV", Toast.LENGTH_SHORT).show()
                cvFilePath = null
            }
        }

        getLm.setEndIconOnClickListener {
            // Si aucune lettre de motivation (lm) n'est sélectionnée
            if (nomLm.text.toString().isEmpty()) {
                // Je lance l'explorateur de fichiers pour sélectionner la lettre de motivation (lm)
                selectFile(myLmLauncher)
            } else {
                nomLm.setText("")
                getLm.setEndIconDrawable(R.drawable.ic_import)
                Toast.makeText(requireContext(), "Vous venez de supprimer votre lettre de motivation", Toast.LENGTH_SHORT).show()
                lmFilePath = null
            }
        }

        // Listener du bouton de validation
        buttonValidate.setOnClickListener{
            // Vérification que chaque input soit remplis
            val allInputsFilled = listOf(inputNom, inputPrenom,inputDateNaissance).all {
                it.text?.isNotEmpty() == true
            }

            if (allInputsFilled) {

                val inputDate : String? = inputDateNaissance?.text?.toString()
                val inputNationalite : String? = autoCompleteTextView?.text?.toString()

                val date: String? = if (!inputDate?.isEmpty()!!) {
                    val originalDateFormat = SimpleDateFormat("dd/MM/yyyy")
                    val parsedDate = originalDateFormat.parse(inputDate.toString())
                    val newDateFormat = SimpleDateFormat("yyyy/MM/dd")
                    newDateFormat.format(parsedDate)
                } else {
                    ""
                }

                var cvByteArray: ByteArray? = null
                selectedFileUri?.let {
                    val inputStream = requireContext().contentResolver.openInputStream(it)
                    cvByteArray = inputStream?.readBytes()
                }

                var cvByteArraylm: ByteArray? = null
                selectedFileUriLm?.let {
                    val inputStream = requireContext().contentResolver.openInputStream(it)
                    cvByteArraylm = inputStream?.readBytes()
                }

                val candidat = CandidatureToSend(
                    user.email,
                    inputPrenom.text.toString(),
                    inputNom.text.toString(),
                    inputNationalite.toString(),
                    date.toString(),
                    cvByteArray,
                    cvFilePath,
                    cvByteArraylm,
                    lmFilePath,
                )

                launchServiceCandidater(candidat)

            }
            else {
                Toast.makeText(requireContext(), "Vous n'avez pas rempli les champs obligatoires", Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }

    private fun selectFile(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        launcher.launch(intent)
    }

    fun launchServiceCandidater(candidat : CandidatureToSend){
        val intent = Intent(requireContext(), CandidatureService::class.java)
        intent.putExtra("candidature", candidat as Serializable)
        requireActivity().startService(intent)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPostCandidatureResult(event: ValidationBooleanEvent) {
        if(event.validateRequest){
            Toast.makeText(requireContext(), "Candidature enregistrée", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(requireContext(), "Erreur lors de l'enregistrement de la candidature", Toast.LENGTH_SHORT).show()
        }
    }

}