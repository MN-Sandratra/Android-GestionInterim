package com.example.gestioninterim.utilisateurInterimaire

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapter.CandidatureInterimaireAdapter
import com.example.gestioninterim.adapter.OfferDecorationItem
import com.example.gestioninterim.models.CandidatureEmployerResult
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.resultEvent.CandidaturesResultEvent
import com.example.gestioninterim.services.CandidatureResultService
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FragmentCandidaturesValidees : Fragment() {


    private lateinit var imageViewEmpty: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var candidaturesAdapter: CandidatureInterimaireAdapter
    private lateinit var listCandidatures : List<CandidatureEmployerResult>
    private lateinit var user : UtilisateurInterimaire
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_interimaire_candidatures_validees, container, false)

        imageViewEmpty = view.findViewById(R.id.imageViewEmpty)

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

        return view
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
        intent.putExtra("status", "acceptée")
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
        updateEmptyView()
    }

}
