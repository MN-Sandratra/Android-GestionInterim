package com.example.gestioninterim.utilisateurInterimaire

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapter.CandidatureAdapter
import com.example.gestioninterim.adapter.CandidatureInterimaireAdapter
import com.example.gestioninterim.adapter.OfferDecorationItem
import com.example.gestioninterim.models.CandidatureEmployerResult
import com.example.gestioninterim.models.CandidatureToSend
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.resultEvent.CandidaturesResultEvent
import com.example.gestioninterim.resultEvent.CandidaturesResultInterimaireEvent
import com.example.gestioninterim.resultEvent.ValidationBooleanEvent
import com.example.gestioninterim.services.CandidatureJobSeekersResultService
import com.example.gestioninterim.services.CandidatureResultService
import com.example.gestioninterim.services.CandidatureService
import com.example.gestioninterim.services.SendCandidatureOfferService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

class FragmentCandidaturesEnvoyees : Fragment() {

    private lateinit var imageViewEmpty: ImageView
    private lateinit var textCandidatureAdd : TextView
    private lateinit var listCandidaturesText : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var candidaturesAdapter: CandidatureInterimaireAdapter
    private lateinit var listCandidatures : List<CandidatureEmployerResult>
    private lateinit var user : UtilisateurInterimaire
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_interimaire_candidatures_envoyees, container, false)

        imageViewEmpty = view.findViewById(R.id.imageViewEmpty)

        textCandidatureAdd = view.findViewById(R.id.addCandidatureText)
        listCandidaturesText = view.findViewById(R.id.listCandidatureText)


        val sharedPreferences = activity?.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurInterimaire::class.java)

        recyclerView = view.findViewById(R.id.vertical_recycler_view_candidatures)
        candidaturesAdapter = CandidatureInterimaireAdapter { candidature ->
            // Gestion des clics sur les éléments de la liste.
            // Remplacez ceci par le code souhaité pour gérer les clics.
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = candidaturesAdapter
        }

        recyclerView.addItemDecoration(OfferDecorationItem())


        launchServiceCandidatures()


        listCandidaturesText.setOnClickListener{
            if(!user.email.isNullOrEmpty())
            {
                launchServiceGetCandidatures(user.email!!)
            }
            else{
                launchServiceGetCandidatures(user.phoneNumber!!)
            }
        }

        textCandidatureAdd.setOnClickListener {
            val activity = activity
            var fragment = FragmentAddCandidature()
            if (activity is MainInterimaireActivity) {
                activity.loadFragment(fragment, R.string.sub_title_consult_missions)
            }
        }



        return view
    }

    override fun onResume() {
        super.onResume()
        launchServiceCandidatures()
    }
    fun launchServiceGetCandidatures(contact : String){
        val intent = Intent(requireContext(), CandidatureJobSeekersResultService::class.java)
        intent.putExtra("contact", contact)
        requireActivity().startService(intent)
    }
    fun launchServiceCandidatures() {
        val intent = Intent(requireContext(), CandidatureResultService::class.java)
        if(user.email.isNullOrEmpty()){
            intent.putExtra("contact", user.phoneNumber)
        }
        else{
            intent.putExtra("contact", user.email)
        }
        intent.putExtra("type", "jobseekers")
        intent.putExtra("status", "en attente")
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
    private fun updateEmptyView() {
        if (candidaturesAdapter.itemCount == 0) {
            imageViewEmpty.visibility = View.VISIBLE
        } else {
            imageViewEmpty.visibility = View.GONE
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetCandidaturesResult(event: CandidaturesResultEvent) {

        candidaturesAdapter.updateCandidatures(event.candidatures)
        listCandidatures = event.candidatures
        updateEmptyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetCandidaturesResultCandidaturesExistantes(event: CandidaturesResultInterimaireEvent) {
        Log.d("CANDIDATURES", "list => ${event.candidatures}")
        val adapter = CandidatureAdapter(requireContext(), event.candidatures)

        AlertDialog.Builder(requireContext())
            .setTitle("Liste des candidatures")
            .setAdapter(adapter) { dialog, which ->

            }
            .setPositiveButton("OK", null)
            .show()
    }


}
