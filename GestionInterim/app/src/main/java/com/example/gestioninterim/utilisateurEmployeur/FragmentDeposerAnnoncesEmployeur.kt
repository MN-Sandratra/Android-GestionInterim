package com.example.gestioninterim.utilisateurEmployeur

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.OfferDAO
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.resultEvent.AddOfferResultEvent
import com.example.gestioninterim.resultEvent.OffersResultEvent
import com.example.gestioninterim.services.OffresService
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.w3c.dom.Text
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class FragmentDeposerAnnoncesEmployeur : Fragment() {

    private lateinit var inputIntituleMission: TextInputEditText
    private lateinit var inputDescriptionMission: TextInputEditText
    private lateinit var inputDateDebutMission: TextInputEditText
    private lateinit var inputDateFinMission: TextInputEditText
    private lateinit var inputRemunerationMission: TextInputEditText
    private lateinit var dropdownMenu: MaterialAutoCompleteTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {


        var view =  inflater.inflate(R.layout.fragment_ajout_offres_employeur, container, false)

        val sharedPreferences = activity?.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        val user = gson.fromJson(jsonUser, UtilisateurEmployeur::class.java)

        inputIntituleMission = view.findViewById(R.id.inputTextIntituleMission)
        inputDescriptionMission = view.findViewById(R.id.inputTextDescriptionMission)
        inputDateDebutMission = view.findViewById(R.id.inputDateDebutMission)
        inputDateFinMission = view.findViewById(R.id.inputDateFinMission)
        inputRemunerationMission = view.findViewById(R.id.inputTextRemunerationMission)
        val buttonAddOffer = view.findViewById<MaterialButton>(R.id.addOfferButton)

        val items = listOf("1", "2", "3+", "Aucune expérience")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
        dropdownMenu = view.findViewById(R.id.inputTextExperienceMission)
        dropdownMenu.setAdapter(adapter)




        val formatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

        inputDateDebutMission.setOnClickListener {
                val datePickerDialog = DatePickerDialog(requireContext())
                datePickerDialog.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateString = formatter.format(selectedDate.time)
                    inputDateDebutMission.setText(dateString)
                }
                datePickerDialog.show()
        }

        inputDateFinMission.setOnClickListener() {
                val datePickerDialog = DatePickerDialog(requireContext())
                datePickerDialog.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateString = formatter.format(selectedDate.time)
                    inputDateFinMission.setText(dateString)
                }
                datePickerDialog.show()
        }

        buttonAddOffer.setOnClickListener(){
            // Vérifier que tous les champs sont remplis
            if (inputIntituleMission.text?.isBlank() == true
                || inputDescriptionMission.text?.isBlank() == true
                || inputDateDebutMission.text?.isBlank() == true
                || inputDateFinMission.text?.isBlank() == true
                || inputRemunerationMission.text?.isBlank() == true
                || dropdownMenu.text.isNullOrBlank()) {
                // Afficher un toast si un ou plusieurs champs sont vides
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {

                val remunerationDouble: Double = inputRemunerationMission.text.toString().toDouble()

                val debutMissionDate: Date =  formatter.parse(inputDateDebutMission.text.toString())

                val finMissionDate: Date =  formatter.parse(inputDateFinMission.text.toString())


                val offer = Offer(user.email1,
                    inputIntituleMission.text.toString(),
                    debutMissionDate,
                    finMissionDate,
                    dropdownMenu.text.toString(),
                    inputDescriptionMission.text.toString(),
                    remunerationDouble,
                    true,
                    "en cours",
                    user.ville,
                    user.address
                    )
                // Tous les champs sont remplis, lancer le service
                launchServiceOffers(offer)  // Supposons que vous ayez une méthode pour lancer votre service
            }
        }

        return view
    }

    fun launchServiceOffers(offer : Offer) {

    val intent = Intent(requireContext(), OffresService::class.java)
    intent.putExtra("isOffer", true)
    intent.putExtra("offerRequest", offer as Serializable)
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPostOfferResult(event: AddOfferResultEvent) {

        val result = event.validation;

        if(result == true){
            AlertDialog.Builder(context)
                .setTitle("Succès")
                .setMessage("L'offre a bien été ajoutée")
                .setPositiveButton(android.R.string.ok, null)
                .show()
            inputIntituleMission.setText("")
            inputDescriptionMission.setText("")
            inputDateDebutMission.setText("")
            inputDateFinMission.setText("")
            inputRemunerationMission.setText("")
            dropdownMenu.setText("Aucune expérience")
        }
        else{
            Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()
        }
    }


}


