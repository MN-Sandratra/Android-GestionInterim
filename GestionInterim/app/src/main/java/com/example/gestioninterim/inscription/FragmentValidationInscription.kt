package com.example.gestioninterim.inscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import android.annotation.SuppressLint
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.gestioninterim.resultEvent.OffersResultEvent
import com.example.gestioninterim.resultEvent.ValidationInscriptionResultEvent
import com.example.gestioninterim.services.InscriptionValidationService
import com.example.gestioninterim.utilisateurEmployeur.SlidesActivity
import com.example.gestioninterim.utilisateurInterimaire.MainInterimaireActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FragmentValidationInscription : Fragment() {

    private lateinit var inputFields: Array<TextInputEditText>
    private var username: String? = null

    @SuppressLint("Range")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inscription_validation, container, false)

        val activity = activity as InscriptionActivity
        val skipButton = activity.findViewById<ImageButton>(R.id.imageButton)
        val inscriptionLaterTV = activity.findViewById<TextView>(R.id.inscriptionLater)
        inscriptionLaterTV.visibility = View.GONE
        skipButton.visibility = View.GONE

        // Définition des boutons
        val clickHere = view.findViewById<TextView>(R.id.clickHere)
        val validation = view.findViewById<MaterialButton>(R.id.buttonValidationInscription)

        // Je récupère l'émail
        val args = arguments
        val email = args?.getString("email")
        username = args?.getString("username")

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

        // Je concatene les chiffres

        // Listener sur le bouton pour valider
        validation.setOnClickListener {
            val code = number1.text.toString() + number2.text.toString() + number3.text.toString() + number4.text.toString()
            if(code.length == 4){
                launchServiceValidation(email.toString(), code)
            }
            else{
                Toast.makeText(requireContext(), "Veuillez saisir les 4 chiffres !", Toast.LENGTH_SHORT).show()
            }
        }


        // Listener sur le bouton si on a pas recu le code
        clickHere.setOnClickListener{
            launchServiceValidation(email.toString(), null)
        }

        return view
    }

    // Lancement du service de validation
    fun launchServiceValidation(email: String, codeValidation: String?){
        val intent = Intent(requireContext(), InscriptionValidationService::class.java)
        intent.putExtra("email", email)
        intent.putExtra("code", codeValidation)
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
    fun onPostValidationResult(event: ValidationInscriptionResultEvent) {
        if (event.type == "jobseekers") {
            val intent = Intent(requireContext(), MainInterimaireActivity::class.java)
            startActivity(intent)
        }
        if(event.type == "employers"){
            val intent = Intent(requireContext(), SlidesActivity::class.java)
            intent.putExtra("nom", username)
            startActivity(intent)
        }
    }
}