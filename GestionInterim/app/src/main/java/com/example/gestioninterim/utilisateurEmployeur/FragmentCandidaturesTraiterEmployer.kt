package com.example.gestioninterim.utilisateurEmployeur

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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
import com.example.gestioninterim.services.AbonnementsService
import com.example.gestioninterim.services.CandidatureResultService
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FragmentCandidaturesTraiterEmployer : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var candidaturesAdapter: CandidatureEmployerAdapter
    private lateinit var listCandidatures : List<CandidatureEmployerResult>

    private lateinit var user : UtilisateurEmployeur
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_menu_top_candidatures_traiter, container, false)

        val sharedPreferences = activity?.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurEmployeur::class.java)

        val searchButton = view.findViewById<MaterialButton>(R.id.validateSearchJob)
        val moreFilter = view.findViewById<ImageButton>(R.id.imageButtonFiltres)
        val deleteFilter = view.findViewById<ImageButton>(R.id.imageButtonDeleteFilters)

        // Initialise le RecyclerView et l'adaptateur.
        recyclerView = view.findViewById(R.id.vertical_recycler_view_offres)
        candidaturesAdapter = CandidatureEmployerAdapter { candidature ->
            // Gestion des clics sur les éléments de la liste.
            // Remplacez ceci par le code souhaité pour gérer les clics.
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = candidaturesAdapter
        }

        recyclerView.addItemDecoration(OfferDecorationItem())


        launchServiceCandidatures()

        return view
    }


    fun launchServiceCandidatures() {
        val intent = Intent(requireContext(), CandidatureResultService::class.java)
        intent.putExtra("contact", user.email1)
        intent.putExtra("type", "employers")
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
    fun onGetCandidaturesResult(event: CandidaturesResultEvent) {

        candidaturesAdapter.updateCandidatures(event.candidatures)
        listCandidatures = event.candidatures
        Log.d("CANDIDATURES", "list => ${listCandidatures[0].candidature.email}")
    }

}