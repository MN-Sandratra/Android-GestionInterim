package com.example.gestioninterim.utilisateurEmployeur

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapter.*
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.OfferDAO
import com.example.gestioninterim.models.OfferEmployerDAO
import com.example.gestioninterim.models.UtilisateurEmployeur
import com.example.gestioninterim.resultEvent.OffersResultEmployerEvent
import com.example.gestioninterim.resultEvent.OffersResultEvent
import com.example.gestioninterim.services.MissionEmployerService
import com.example.gestioninterim.services.OffresService
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable

class FragmentAnnoncesDeposees : Fragment(), FilterDialogCallbackEmployer{

    private lateinit var recyclerView: RecyclerView
    private lateinit var offerAdapter: OfferEmployerAdapter
    private lateinit var listOffers: List<Offer>
    private lateinit var inputMetier : TextInputEditText
    private var filterMetier: String = ""
    private var filterDateDebut: String = ""
    private var filterDateFin: String = ""
    private lateinit var user : UtilisateurEmployeur


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        var view =  inflater.inflate(R.layout.fragment_menu_top_annonces_deposees, container, false)

        val sharedPreferences = activity?.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gson = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gson.fromJson(jsonUser, UtilisateurEmployeur::class.java)


        val searchButton = view.findViewById<MaterialButton>(R.id.validateSearchJob)
        val moreFilter = view.findViewById<ImageButton>(R.id.imageButtonFiltres)
        val deleteFilter = view.findViewById<ImageButton>(R.id.imageButtonDeleteFilters)

        // Initialise le RecyclerView et l'adaptateur.
        recyclerView = view.findViewById(R.id.vertical_recycler_view_offres)
        offerAdapter = OfferEmployerAdapter { offer ->
            // Gestion des clics sur les éléments de la liste.
            // Remplacez ceci par le code souhaité pour gérer les clics.
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = offerAdapter
        }

        // Ajout de l'écart entre chaque offre
        recyclerView.addItemDecoration(OfferDecorationItem())

        launchServiceOffers()

        // Listener du bouton de validation
        searchButton.setOnClickListener {
            offerAdapter.updateOffers(listOffers)
        }

        // Listener du bouton d'ajout de filtres
        moreFilter.setOnClickListener{
            FilterPopupEmployer(requireContext(), this, filterMetier, filterDateDebut, filterDateFin).show()
        }

        deleteFilter.setOnClickListener{
            launchServiceOffers()
        }

        return view
    }

    fun launchServiceOffers() {

        val offerRequest = OfferEmployerDAO(email = user.email1)
        val intent = Intent(requireContext(), MissionEmployerService::class.java)
        intent.putExtra("offerRequest", offerRequest as Serializable)
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
    fun onGetOffersResult(event: OffersResultEmployerEvent) {
        println(event.offres)
        offerAdapter.updateOffers(event.offres)
        listOffers = event.offres
    }

    override fun onFiltersApplied(metier: String, dateDebut: String, dateFin: String) {

        filterMetier = metier
        filterDateDebut = dateDebut
        filterDateFin = dateFin

        val offerRequest = OfferEmployerDAO(email = user.email1, metier = filterMetier, dateDebut = filterDateDebut, dateFin = filterDateFin)
        val intent = Intent(requireContext(), MissionEmployerService::class.java)
        intent.putExtra("offerRequest", offerRequest as Serializable)
        requireContext().startService(intent)
    }

}