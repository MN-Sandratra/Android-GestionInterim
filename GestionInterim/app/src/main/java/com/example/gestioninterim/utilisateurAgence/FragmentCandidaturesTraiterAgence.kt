package com.example.gestioninterim.utilisateurAgence

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapter.CandidatureEmployerAdapter
import com.example.gestioninterim.adapter.OfferDecorationItem
import com.example.gestioninterim.models.CandidatureEmployerResult
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.resultEvent.CandidaturesResultEvent
import com.example.gestioninterim.services.CandidatureResultService
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FragmentCandidaturesTraiterAgence : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var candidaturesAdapter: CandidatureEmployerAdapter
    private lateinit var listCandidatures : List<CandidatureEmployerResult>
    private lateinit var imageViewEmpty: ImageView


    private lateinit var user : UtilisateurEmployeur
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_menu_top_candidatures_traiter, container, false)
        imageViewEmpty = view.findViewById(R.id.imageViewEmpty)

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
            val activity = activity
            if (activity is MainAgenceActivity) {
                val gson = Gson()
                val candidatureJson = gson.toJson(candidature)
                val fragment = FragmentCandidaturesDetailsAgence()
                val args = Bundle()
                args.putString("candidatureATraiter", candidatureJson)
                fragment.arguments = args

                activity.loadFragment(fragment, R.string.sub_title_consult_missions)
            }
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
        intent.putExtra("status", "en attente")
        Log.d("Affichage", "=> A TRAITER 1")
        requireContext().startService(intent)
    }

    override fun onResume() {
        super.onResume()
        launchServiceCandidatures()
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
        Log.d("Affichage", "=> A TRAITER 2")
        updateEmptyView()
    }

}