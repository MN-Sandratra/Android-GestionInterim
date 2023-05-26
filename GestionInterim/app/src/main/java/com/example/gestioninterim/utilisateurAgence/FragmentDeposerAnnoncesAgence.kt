package com.example.gestioninterim.utilisateurAgence

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputType
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.models.JsonOffer
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.resultEvent.AddOfferResultEvent
import com.example.gestioninterim.services.OffresAgenceService
import com.example.gestioninterim.services.OffresService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class FragmentDeposerAnnoncesAgence : Fragment() {

    private lateinit var openDocument: ActivityResultLauncher<Array<String>>
    private lateinit var currentEditText: TextInputEditText
    private lateinit var user : UtilisateurEmployeur
    private lateinit var container : LinearLayout
    private val editTextList = mutableListOf<TextInputEditText>()
    private val editTextToContent = mutableMapOf<TextInputEditText, String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { readJsonFile(it, currentEditText) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_ajout_offres_agence, container, false)

        val sharedPreferences = activity?.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurEmployeur::class.java)


        val plusButton: TextView = view.findViewById(R.id.plusText)
        val minusButton: TextView = view.findViewById(R.id.moinsText)
        this.container = view.findViewById(R.id.container)



        plusButton.setOnClickListener {
            createTextInputLayout(this.container)
        }

        for (i in 1..2) {
            createTextInputLayout(this.container)
        }

        minusButton.setOnClickListener {
            if (this.container.childCount > 0) {
                this.container.removeViewAt(this.container.childCount - 1)
                editTextList.removeAt(editTextList.lastIndex)
            }
        }

        val buttonAddOffer = view.findViewById<MaterialButton>(R.id.addOfferButton)

        buttonAddOffer.setOnClickListener {
            val offers = editTextList.mapNotNull { editText ->
                val json = editTextToContent[editText]
                try {
                    val jsonOffer = gson.fromJson(json, JsonOffer::class.java)
                    transformJsonOfferToOffer(jsonOffer)
                } catch (e: JsonSyntaxException) {
                    null
                }
            }

            launchServiceOffers(offers)
        }


        return view
    }


    private fun createTextInputLayout(container: LinearLayout) {
        val newTextInputEditText = TextInputEditText(ContextThemeWrapper(requireContext(), R.style.MyTextInputLayout)).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = InputType.TYPE_CLASS_TEXT
            setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        val newTextInputLayout = TextInputLayout(ContextThemeWrapper(requireContext(), R.style.MyTextInputLayout)).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setHintTextColor(ColorStateList.valueOf(Color.WHITE))
            hint = "Ajouter une offre"
            setBoxBackgroundColorResource(R.color.darkblue)
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            endIconMode = TextInputLayout.END_ICON_CUSTOM
            endIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_import)
            setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)))
            startIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_document)
            setStartIconTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)))

            setEndIconOnClickListener {
                currentEditText = newTextInputEditText
                openDocument.launch(arrayOf("application/json"))
            }

            addView(newTextInputEditText)
        }

        container.addView(newTextInputLayout)
        editTextList.add(newTextInputEditText)
    }

    fun launchServiceOffers(offers: List<Offer>) {
        val intent = Intent(requireContext(), OffresAgenceService::class.java)
        intent.putExtra("isOffer", true)

        val gson = Gson()
        val jsonOffers = gson.toJson(offers)

        intent.putExtra("offerRequest", jsonOffers)
        requireContext().startService(intent)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
    private fun readJsonFile(uri: Uri, editText: TextInputEditText) {
        val contentResolver = activity?.contentResolver ?: return
        val inputStream = contentResolver.openInputStream(uri) ?: return

        val fileContent: String
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            fileContent = reader.readText()
        }

        val fileName = getFileName(uri)
        editText.setText(fileName)
        editTextToContent[editText] = fileContent
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = activity?.contentResolver?.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "unknown"
    }

    private fun transformJsonOfferToOffer(jsonOffer: JsonOffer): Offer {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Adjust according to your date format in JSON
        return Offer(
            employeur = user.email1,
            intitule = jsonOffer.intitule,
            dateDebut = dateFormat.parse(jsonOffer.dateDebut),
            dateFin = dateFormat.parse(jsonOffer.dateFin),
            experienceRequise = jsonOffer.experienceRequise,
            description = jsonOffer.description,
            tauxHoraire = jsonOffer.tauxHoraire,
            heureDebut = jsonOffer.heureDebut,
            disponibilite = jsonOffer.disponibilite,
            etat = jsonOffer.etat,
            ville = jsonOffer.ville,
            adressePostale = jsonOffer.adressePostale
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPostOfferResult(event: AddOfferResultEvent) {
        val result = event.validation

        if (result == true) {
            AlertDialog.Builder(context)
                .setTitle("Succès")
                .setMessage("Les offres ont bien été ajoutées !")
                .setPositiveButton(android.R.string.ok, null)
                .show()

            container.removeAllViews()
            editTextList.clear()

            for (i in 1..2) {
                createTextInputLayout(container)
            }

        } else {
            Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()
        }
    }
}

