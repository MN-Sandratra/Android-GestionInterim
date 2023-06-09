package com.example.gestioninterim.inscription

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.login.LoginActivity
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.resultEvent.ValidationBooleanEvent
import com.example.gestioninterim.services.InscriptionService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class FragmentAgenceInscription : Fragment() {

    private lateinit var inputNomEntreprise : TextInputEditText
    private lateinit var inputMailContact1 : TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_inscription_agence, container, false)
        val clickHere = view.findViewById<TextView>(R.id.clickHere)

        clickHere.setOnClickListener{
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

        }

        // Bouton de validation
        val buttonValidate = view.findViewById<MaterialButton>(R.id.buttonValidationInscription)

        // Input employeurs
        inputNomEntreprise = view.findViewById(R.id.inputTextNomEntreprise)
        val inputNomService = view.findViewById<TextInputEditText>(R.id.inputTextNomService)
        val inputNomSousService = view.findViewById<TextInputEditText>(R.id.inputTextNomSousService)
        val inputMdp = view.findViewById<TextInputEditText>(R.id.inputTextPassword)
        val inputConfirmMdp = view.findViewById<TextInputEditText>(R.id.inputTextConfirmPassword)
        val inputNumeroEntite = view.findViewById<TextInputEditText>(R.id.inputTextNumeroEntite)
        val inputAdresse = view.findViewById<TextInputEditText>(R.id.inputTextAdresse)
        val inputVille = view.findViewById<TextInputEditText>(R.id.inputTextVille)

        val inputNomContact1 = view.findViewById<TextInputEditText>(R.id.inputTextNomContact1)
        val inputTelephoneContact1 = view.findViewById<TextInputEditText>(R.id.inputTextTelephoneContact1)
        inputMailContact1 = view.findViewById(R.id.inputTextMailContact1)
        val inputNomContact2 = view.findViewById<TextInputEditText>(R.id.inputTextNomContact2)
        val inputTelephoneContact2 = view.findViewById<TextInputEditText>(R.id.inputTextTelephoneContact2)
        val inputMailContact2 = view.findViewById<TextInputEditText>(R.id.inputTextMailContact2)

        // Listener du bouton de validation
        buttonValidate.setOnClickListener{

            val isMdpMatching = inputMdp.text.toString() == inputConfirmMdp.text.toString()

            // Test du remplissage des champs et de la correspondance des mots de passe
            val allInputsFilled = listOf(inputNomEntreprise, inputNumeroEntite,inputAdresse, inputMailContact1 ,inputMdp, inputConfirmMdp, inputVille).all {
                it.text?.isNotEmpty() == true
            } && isMdpMatching

            // Si tous les champs on était remplis
            if (allInputsFilled) {

                val inputNomServiceText : String? = inputNomService?.text?.toString()
                val inputNomSousServiceText: String? = inputNomSousService?.text?.toString()
                val inputNomContact1Text: String? = inputNomContact1?.text?.toString()
                val inputTelephoneContact1Text: String? = inputTelephoneContact1?.text?.toString()
                val inputNomContact2Text : String? = inputNomContact2?.text?.toString()
                val inputMailContact2Text: String? = inputMailContact2?.text?.toString()
                val inputTelephoneContact2Text: String? = inputTelephoneContact2?.text?.toString()

                val inputPasswordEncrypt : String = hashPassword(inputMdp.text.toString())

                val user = UtilisateurEmployeur(
                    "",
                    inputNomEntreprise.text.toString(),
                    inputNomServiceText,
                    inputNomSousServiceText,
                    inputNumeroEntite.text.toString(),
                    inputNomContact1Text,
                    inputNomContact2Text,
                    inputMailContact1.text.toString(),
                    inputPasswordEncrypt,
                    inputMailContact2Text,
                    inputTelephoneContact1Text,
                    inputTelephoneContact2Text,
                    inputAdresse.text.toString(),
                    inputVille.text.toString(),
                    "",
                    "",
                    ""
                )

                // Lancement du service d'inscription
                launchServiceInscription(user)



            } else if (!isMdpMatching) {
                Toast.makeText(requireContext(), "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireContext(), "Vous n'avez pas rempli les champs obligatoires", Toast.LENGTH_SHORT).show()
            }

        }

        return view
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    // Lancement du service d'inscription
    fun launchServiceInscription(user : UtilisateurEmployeur){
        val intent = Intent(requireContext(), InscriptionService::class.java)
        intent.putExtra("utilisateur", user as Serializable)
        Log.d("Affichage", "===> LAA")

        intent.putExtra("type", "agence")
        requireActivity().startService(intent)
    }

    // Fonction permettant de hasher le mot de passe
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPostInscriptionResult(event: ValidationBooleanEvent) {
        Log.d("Affichage", "===> ${event.validateRequest}")
        if(event.validateRequest){
            // Lancement du fragment de validation
            val fragment = FragmentValidationInscription()
            val args = Bundle()
            args.putString("username", inputNomEntreprise.text.toString())
            args.putString("email", inputMailContact1.text.toString())
            fragment.arguments = args
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.containerInscription, fragment).commit()
        }
        else{
            Toast.makeText(requireContext(), "L'émail est déjà utilisé !", Toast.LENGTH_SHORT).show()
        }
    }

}