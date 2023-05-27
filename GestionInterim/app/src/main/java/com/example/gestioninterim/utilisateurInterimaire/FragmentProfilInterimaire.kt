package com.example.gestioninterim.utilisateurInterimaire
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.login.LoginActivity
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.resultEvent.ProfilEmployerResultEvent
import com.example.gestioninterim.resultEvent.ProfilInterimaireResultEvent
import com.example.gestioninterim.services.ProfilEmployerService
import com.example.gestioninterim.services.ProfilInterimaireService
import com.example.gestioninterim.services.SuppresionCompteService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

class FragmentProfilInterimaire : Fragment() {

    private lateinit var inputTextNom: TextInputEditText
    private lateinit var inputTextPrenom: TextInputEditText
    private lateinit var inputTextMail: TextInputEditText
    private lateinit var inputTextTelephone: TextInputEditText
    private lateinit var inputTextDateNaissance: TextInputEditText
    private lateinit var inputTextVille: TextInputEditText
    private lateinit var inputTextNationalite: TextInputEditText
    private lateinit var inputTextCommentaires: TextInputEditText
    private lateinit var buttonModify: MaterialButton
    private val sharedPreferences: SharedPreferences
        get() = requireActivity().getSharedPreferences("user_infos", Context.MODE_PRIVATE)

    private val gson = Gson()
    private lateinit var user : UtilisateurInterimaire

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profil_interimaire, container, false)

        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurInterimaire::class.java)

        val deconnexionText = view.findViewById<TextView>(R.id.deconnexionText)
        val suppressionText = view.findViewById<TextView>(R.id.suppressionText)


        inputTextNom = view.findViewById(R.id.inputTextNom)
        inputTextPrenom = view.findViewById(R.id.inputTextPrenom)
        inputTextMail = view.findViewById(R.id.inputTextMail)
        inputTextTelephone = view.findViewById(R.id.inputTextTelephone)
        inputTextDateNaissance = view.findViewById(R.id.inputDateNaissance)
        inputTextVille = view.findViewById(R.id.inputVille)
        inputTextNationalite = view.findViewById(R.id.inputNationnalite)
        inputTextCommentaires = view.findViewById(R.id.inputCommentaires)
        buttonModify = view.findViewById(R.id.modifyButton)


        setupTextField(inputTextNom)
        setupTextField(inputTextPrenom)
        setupTextField(inputTextMail)
        setupTextField(inputTextTelephone)
        setupTextField(inputTextDateNaissance)
        setupTextField(inputTextVille)
        setupTextField(inputTextNationalite)
        setupTextField(inputTextCommentaires)

        // Assurez-vous que l'objet `user` n'est pas null avant d'affecter les valeurs
        if (user != null) {
            inputTextNom.setText(user.lastName)
            inputTextPrenom.setText(user.firstName)
            inputTextMail.setText(user.email ?: "non défini")
            inputTextTelephone.setText(user.phoneNumber ?: "non défini")
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            if (user.dateOfBirth != null) {
                val date = inputFormat.parse(user.dateOfBirth)
                inputTextDateNaissance.setText(outputFormat.format(date))
            }
            else{
                inputTextDateNaissance.setText("non défini")
            }
            inputTextVille.setText(user.city ?: "non défini")
            inputTextNationalite.setText(user.nationality ?: "non défini")
            inputTextCommentaires.setText(user.comments ?: "non défini")

            inputTextNom.setTextColor(if (user.lastName == null) Color.GRAY else Color.WHITE)
            inputTextPrenom.setTextColor(if (user.firstName == null) Color.GRAY else Color.WHITE)
            inputTextMail.setTextColor(if (user.email == null) Color.GRAY else Color.WHITE)
            inputTextTelephone.setTextColor(if (user.phoneNumber == null) Color.GRAY else Color.WHITE)
            inputTextDateNaissance.setTextColor(if (user.dateOfBirth == null) Color.GRAY else Color.WHITE)
            inputTextVille.setTextColor(if (user.city == null) Color.GRAY else Color.WHITE)
            inputTextCommentaires.setTextColor(if (user.comments == null) Color.GRAY else Color.WHITE)
            inputTextNationalite.setTextColor(if (user.nationality == null) Color.GRAY else Color.WHITE)

        }


        buttonModify.setOnClickListener {

            val lastName = getValueOrSetNull(inputTextNom.text.toString())
            val firstName = getValueOrSetNull(inputTextPrenom.text.toString())
            val email = getValueOrSetNull(inputTextMail.text.toString())
            val phoneNumber = getValueOrSetNull(inputTextTelephone.text.toString())
            val dateOfBirth = getValueOrSetNull(inputTextDateNaissance.text.toString())
            val city = getValueOrSetNull(inputTextVille.text.toString())
            val nationality = getValueOrSetNull(inputTextNationalite.text.toString())
            val comments = getValueOrSetNull(inputTextCommentaires.text.toString())

            if (lastName == null || firstName == null || email == null) {
                Toast.makeText(
                    context,
                    "Veuillez remplir tous les champs obligatoires",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val updatedUser = UtilisateurInterimaire(
                    _id=user._id,
                    lastName = lastName,
                    firstName = firstName,
                    email = email,
                    phoneNumber = phoneNumber,
                    dateOfBirth = dateOfBirth,
                    city = city,
                    nationality = nationality,
                    comments = comments,
                    password = user.password
                )
                Log.d("Affichage", "===> JE SUIS LAA2")
                launchServiceProfilUpdate(updatedUser)
            }
        }

        deconnexionText.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Déconnexion")
            builder.setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
            builder.setPositiveButton("Oui") { dialog, _ ->

                sharedPreferences?.edit()?.clear()?.apply()
                // Démarrer LoginActivity
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                // Terminer l'activité actuelle
                activity?.finish()
                dialog.dismiss()
            }
            builder.setNegativeButton("Non") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        suppressionText.setOnClickListener {

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Suppression de compte")
            builder.setMessage("Êtes-vous sûr de vouloir supprimer votre compte ?")
            builder.setPositiveButton("Oui") { dialog, _ ->

                launchServiceDeleteAccount()

                sharedPreferences?.edit()?.clear()?.apply()
                // Démarrer LoginActivity
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
                // Terminer l'activité actuelle
                activity?.finish()
                dialog.dismiss()
            }
            builder.setNegativeButton("Non") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
        return view
    }


    fun launchServiceProfilUpdate(user : UtilisateurInterimaire) {

        val intent = Intent(requireContext(), ProfilInterimaireService::class.java)
        intent.putExtra("userRequest", user as Serializable)
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
    fun onPutProfilUpdate(event: ProfilInterimaireResultEvent) {
        Log.d("Affichage", "===> JE SUIS LAA")
        val editor = sharedPreferences!!.edit()
        val updatedUserJson = gson.toJson(event.employer)
        editor.putString("user", updatedUserJson)
        editor.apply()
        Toast.makeText(requireContext(), "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show()
    }

    fun launchServiceDeleteAccount() {
        Log.d("Affichage", "===> JE SUIS LAA231")
        val intent = Intent(requireContext(), SuppresionCompteService::class.java)
        if(!user.email.isNullOrEmpty()){
            intent.putExtra("contact", user.email)
        }
        else{
            intent.putExtra("contact", user.phoneNumber)
        }
        intent.putExtra("type", "jobseekers")
        requireContext().startService(intent)
    }


    private fun getValueOrSetNull(text: String): String? {
        return if (text == "non défini") null else text
    }


    // Ajoutez cette fonction dans votre classe FragmentProfilEmployeur
    private fun setupTextField(inputText: TextInputEditText) {
        inputText.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus && inputText.text.toString() == "non défini") {
                inputText.setText("")
                inputText.setTextColor(Color.WHITE)
            } else if (!hasFocus && inputText.text.toString().isEmpty()) {
                inputText.setText("non défini")
                inputText.setTextColor(Color.GRAY)
            } else {
                inputText.setTextColor(Color.WHITE)
            }
        }

        inputText.setOnClickListener {
            if (inputText.text.toString() == "non défini") {
                inputText.setText("")
                inputText.setTextColor(Color.WHITE)
            }
        }
    }
}
