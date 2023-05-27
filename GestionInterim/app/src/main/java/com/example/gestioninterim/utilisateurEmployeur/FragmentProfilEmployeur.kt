package com.example.gestioninterim.utilisateurEmployeur
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.login.LoginActivity
import com.example.gestioninterim.models.OfferEmployerDAO
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.resultEvent.OffersResultEmployerEvent
import com.example.gestioninterim.resultEvent.ProfilEmployerResultEvent
import com.example.gestioninterim.services.AbonnementsService
import com.example.gestioninterim.services.MissionEmployerService
import com.example.gestioninterim.services.ProfilEmployerService
import com.example.gestioninterim.services.SuppresionCompteService
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable

class FragmentProfilEmployeur : Fragment() {

    private lateinit var inputTextNomEmployeur: TextInputEditText
    private lateinit var inputTextNomService: TextInputEditText
    private lateinit var inputTextNomSousService: TextInputEditText
    private lateinit var inputTextNumeroEntite: TextInputEditText
    private lateinit var inputTextNomContact1: TextInputEditText
    private lateinit var inputTextNomContact2: TextInputEditText
    private lateinit var inputTextMailContact1: TextInputEditText
    private lateinit var inputTextMailContact2: TextInputEditText
    private lateinit var inputTextPhoneContact1: TextInputEditText
    private lateinit var inputTextPhoneContact2: TextInputEditText
    private lateinit var inputTextAdresse: TextInputEditText
    private lateinit var inputTextVile: TextInputEditText
    private lateinit var buttonModify : MaterialButton
    private val sharedPreferences: SharedPreferences
        get() = requireActivity().getSharedPreferences("user_infos", Context.MODE_PRIVATE)

    private val gson = Gson()
    private lateinit var user : UtilisateurEmployeur

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profil_employeur, container, false)

        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurEmployeur::class.java)

        val deconnexionText = view.findViewById<TextView>(R.id.deconnexionText)
        val suppressionText = view.findViewById<TextView>(R.id.suppressionText)

        inputTextNomEmployeur = view.findViewById(R.id.inputTextNomEmployeur)
        inputTextNomService = view.findViewById(R.id.inputTextNomService)
        inputTextNomSousService = view.findViewById(R.id.inputTextNomSousService)
        inputTextNumeroEntite = view.findViewById(R.id.inputTextNumeroEntite)
        inputTextNomContact1 = view.findViewById(R.id.inputTextNomContact1)
        inputTextNomContact2 = view.findViewById(R.id.inputTextNomContact2)
        inputTextMailContact1 = view.findViewById(R.id.inputTextMailContact1)
        inputTextMailContact2 = view.findViewById(R.id.inputTextMailContact2)
        inputTextPhoneContact1 = view.findViewById(R.id.inputTextPhoneContact1)
        inputTextPhoneContact2 = view.findViewById(R.id.inputTextPhoneContact2)
        inputTextAdresse = view.findViewById(R.id.inputTextAdresse)
        inputTextVile = view.findViewById(R.id.inputTextVile)
        buttonModify = view.findViewById(R.id.modifyButton)

        setupTextField(inputTextNomEmployeur)
        setupTextField(inputTextNomService)
        setupTextField(inputTextNomSousService)
        setupTextField(inputTextNumeroEntite)
        setupTextField(inputTextNomContact1)
        setupTextField(inputTextNomContact2)
        setupTextField(inputTextMailContact1)
        setupTextField(inputTextMailContact2)
        setupTextField(inputTextPhoneContact1)
        setupTextField(inputTextPhoneContact2)
        setupTextField(inputTextAdresse)
        setupTextField(inputTextVile)

        // Assurez-vous que l'objet `user` n'est pas null avant d'affecter les valeurs
        if (user != null) {
            inputTextNomEmployeur.setText(user.companyName)
            inputTextNomService.setText(user.department ?: "non défini")
            inputTextNomSousService.setText(user.subDepartment ?: "non défini")
            inputTextNumeroEntite.setText(user.nationalNumber)
            inputTextNomContact1.setText(user.contactPerson1 ?: "non défini")
            inputTextNomContact2.setText(user.contactPerson2 ?: "non défini")
            inputTextMailContact1.setText(user.email1)
            inputTextMailContact2.setText(user.email2 ?: "non défini")
            inputTextPhoneContact1.setText(user.phone1 ?: "non défini")
            inputTextPhoneContact2.setText(user.phone2 ?: "non défini")
            inputTextAdresse.setText(user.address)
            inputTextVile.setText(user.ville)

            inputTextNomService.setTextColor(if (user.department == null) Color.GRAY else Color.WHITE)
            inputTextNomSousService.setTextColor(if (user.subDepartment == null) Color.GRAY else Color.WHITE)
            inputTextNomContact1.setTextColor(if (user.contactPerson1 == null) Color.GRAY else Color.WHITE)
            inputTextNomContact2.setTextColor(if (user.contactPerson2 == null) Color.GRAY else Color.WHITE)
            inputTextMailContact2.setTextColor(if (user.email2 == null) Color.GRAY else Color.WHITE)
            inputTextPhoneContact1.setTextColor(if (user.phone1 == null) Color.GRAY else Color.WHITE)
            inputTextPhoneContact2.setTextColor(if (user.phone2 == null) Color.GRAY else Color.WHITE)

        }


        buttonModify.setOnClickListener {

            val companyName = getValueOrSetNull(inputTextNomEmployeur.text.toString())
            val nationalNumber = getValueOrSetNull(inputTextNumeroEntite.text.toString())
            val address = getValueOrSetNull(inputTextAdresse.text.toString())
            val ville = getValueOrSetNull(inputTextVile.text.toString())

            if (companyName == null || nationalNumber == null || address == null || ville == null) {
                Toast.makeText(
                    context,
                    "Veuillez remplir tous les champs obligatoires",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val updatedUser = UtilisateurEmployeur(
                    _id=user._id,
                    companyName = inputTextNomEmployeur.text.toString(),
                    department = getValueOrSetNull(inputTextNomService.text.toString()),
                    subDepartment = getValueOrSetNull(inputTextNomSousService.text.toString()),
                    nationalNumber = inputTextNumeroEntite.text.toString(),
                    contactPerson1 = getValueOrSetNull(inputTextNomContact1.text.toString()),
                    contactPerson2 = getValueOrSetNull(inputTextNomContact2.text.toString()),
                    email1 = user.email1,
                    email2 = getValueOrSetNull(inputTextMailContact2.text.toString()),
                    phone1 = getValueOrSetNull(inputTextPhoneContact1.text.toString()),
                    phone2 = getValueOrSetNull(inputTextPhoneContact2.text.toString()),
                    address = inputTextAdresse.text.toString(),
                    ville = inputTextVile.text.toString(),
                    linkedin = null,
                    facebook = null,
                    website = null,
                    password = user.password
                )
                launchServiceOffers(updatedUser)
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


    fun launchServiceOffers(user : UtilisateurEmployeur) {

        val intent = Intent(requireContext(), ProfilEmployerService::class.java)
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
    fun onPutProfilUpdate(event: ProfilEmployerResultEvent) {
        val editor = sharedPreferences!!.edit()
        val updatedUserJson = gson.toJson(event.employer)
        editor.putString("user", updatedUserJson)
        editor.apply()
        Toast.makeText(requireContext(), "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show()
    }

    fun launchServiceDeleteAccount() {
        val intent = Intent(requireContext(), SuppresionCompteService::class.java)
        intent.putExtra("contact", user.email1)
        intent.putExtra("type", "employers")
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
