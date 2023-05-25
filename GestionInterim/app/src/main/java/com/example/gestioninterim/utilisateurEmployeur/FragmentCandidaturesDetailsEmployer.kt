package com.example.gestioninterim.utilisateurEmployeur

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapter.CandidatureEmployerAdapter
import com.example.gestioninterim.adapter.OfferDecorationItem
import com.example.gestioninterim.adapter.OfferEmployerAdapter
import com.example.gestioninterim.models.Candidature
import com.example.gestioninterim.models.CandidatureEmployerResult
import com.example.gestioninterim.models.OfferResult
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.resultEvent.AbonnementsResultEvent
import com.example.gestioninterim.resultEvent.CandidaturesResultEvent
import com.example.gestioninterim.resultEvent.ValidationBooleanEvent
import com.example.gestioninterim.services.AbonnementsService
import com.example.gestioninterim.services.CandidatureEmployerTraitementService
import com.example.gestioninterim.services.CandidatureResultService
import com.example.gestioninterim.utilisateurInterimaire.FragmentOfferDetails
import com.example.gestioninterim.utilisateurInterimaire.MainInterimaireActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class FragmentCandidaturesDetailsEmployer : Fragment() {

    private lateinit var user : UtilisateurEmployeur
    private lateinit var candidatureEmployerResult: CandidatureEmployerResult
    private lateinit var boutonCandidature : MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_employeur_candidature_details, container, false)

        val sharedPreferences = activity?.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurEmployeur::class.java)

        val candidatureJson = arguments?.getString("candidatureATraiter")
        candidatureEmployerResult = gson.fromJson(candidatureJson, CandidatureEmployerResult::class.java)


        view.findViewById<TextInputEditText>(R.id.inputTextFirstName).setText(candidatureEmployerResult.candidature.firstName)
        view.findViewById<TextInputEditText>(R.id.inputTextLastName).setText(candidatureEmployerResult.candidature.lastName)
        view.findViewById<TextInputEditText>(R.id.inputTextNationality).setText(candidatureEmployerResult.candidature.nationality)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(candidatureEmployerResult.candidature.dateOfBirth)
        view.findViewById<TextInputEditText>(R.id.inputTextDateNaissance).setText(outputFormat.format(date))
        view.findViewById<TextInputEditText>(R.id.inputTextCv).setText(candidatureEmployerResult.candidature.cv)
        view.findViewById<TextInputEditText>(R.id.inputTextLm).setText(candidatureEmployerResult.candidature.lm ?: "N/A")

        val textInputLayoutCv = view.findViewById<TextInputLayout>(R.id.inputLayoutCv)
        val textInputLayoutLM = view.findViewById<TextInputLayout>(R.id.inputLayoutLm)

        textInputLayoutCv.setEndIconOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Téléchargement - CV")
            builder.setMessage("Voulez-vous télécharger le CV?")

            builder.setPositiveButton("Oui") { dialog, which ->
                // Code pour télécharger le CV ici
            }

            builder.setNegativeButton("Non") { dialog, which ->
                // L'utilisateur a décidé de ne pas télécharger le CV
            }

            builder.show()
        }

        textInputLayoutLM.setEndIconOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Téléchargement - Lettre de motivation")
            builder.setMessage("Voulez-vous télécharger la lettre de motivation ?")

            builder.setPositiveButton("Oui") { dialog, which ->
                // Code pour télécharger le CV ici
            }

            builder.setNegativeButton("Non") { dialog, which ->
                // L'utilisateur a décidé de ne pas télécharger le CV
            }

            builder.show()
        }


        boutonCandidature = view.findViewById(R.id.boutonGererCandidature)

        boutonCandidature.setOnClickListener{

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Gestion de la candidature")

            // Ajouter les options
            builder.setItems(arrayOf("Accepter", "Refuser", "Mettre en attente")) { dialog, which ->
                when (which) {
                    0 -> {
                        // L'utilisateur a cliqué sur "Accepter"
                        launchServiceCandidaturesTraitement("acceptée")
                    }
                    1 -> {
                        // L'utilisateur a cliqué sur "Refuser"
                        launchServiceCandidaturesTraitement("refusée")
                    }
                    2 -> {
                        val activity = activity
                        if (activity is MainEmployeurActivity) {
                            val fragment = FragmentCandidaturesTraiterEmployer()
                            activity.loadFragment(fragment, R.string.sub_title_consult_missions)
                        }
                    }
                }
            }

            // Afficher l'AlertDialog
            builder.show()

        }

        return view
    }


    fun launchServiceCandidaturesTraitement(traitement : String) {
        val intent = Intent(requireContext(), CandidatureEmployerTraitementService::class.java)
        intent.putExtra("traitement", traitement)
        intent.putExtra("identifiantCandidature", candidatureEmployerResult.candidature.identifiant)
        intent.putExtra("identifiantOffre", candidatureEmployerResult.offre.identifiant)

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
    fun onPostCandidaturesResult(event: ValidationBooleanEvent) {

        Log.d("Afficahge", "===> ${event.validateRequest}")

        if(event.validateRequest){
            val activity = activity
            if (activity is MainEmployeurActivity) {
                val fragment = FragmentConsultEmployer()
                activity.loadFragment(fragment, R.string.sub_title_consult_missions)
            }
        }

    }

}