package com.example.gestioninterim.commonFragments
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapter.OfferAdapter
import com.example.gestioninterim.adapter.OfferDecorationItem
import com.example.gestioninterim.resultEvent.OffersResultEvent
import com.example.gestioninterim.services.OffresService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.Manifest;
import android.widget.ProgressBar
import android.widget.Toast
import com.example.gestioninterim.models.Offer
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class FragmentOffer : Fragment() {

    private lateinit var offerAdapter: OfferAdapter
    private lateinit var recyclerView: RecyclerView
    private val locationRequestCode = 1000
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var progressBar: ProgressBar
    private lateinit var listOffers: List<Offer>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_menu_top_search, container, false)

        // Initialisation des variables layout
        val inputMetier = view.findViewById<TextInputEditText>(R.id.editMetier)
        val searchButton = view.findViewById<MaterialButton>(R.id.validateSearchJob)


        // Initialise le RecyclerView et l'adaptateur.
        recyclerView = view.findViewById(R.id.vertical_recycler_view_offres)
        offerAdapter = OfferAdapter { offer ->
            // Gestion des clics sur les éléments de la liste.
            // Remplacez ceci par le code souhaité pour gérer les clics.
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = offerAdapter
        }

        // Ajout de l'écart entre chaque offre
        recyclerView.addItemDecoration(OfferDecorationItem())

        // Permission de localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        requestLocationPermission()

        searchButton.setOnClickListener {
            println("L'input text est : ${inputMetier.text}")
            println("L'input text est : ${inputMetier.text!!.isEmpty()}")
            if(inputMetier.text!!.isEmpty() && offerAdapter.getOffers().size != listOffers.size){
                offerAdapter.updateOffers(listOffers)
            }
            else{
                getOffersByJob(inputMetier.text.toString(), listOffers)
            }
        }

        return view
    }

    private fun getOffersByJob(job : String, offerList : List<Offer>){

        val offers = offerList
        val offersResult : MutableList<Offer> = mutableListOf()
        for (offer in offers){
            if(offer.intitule.contains(job)){
                offersResult.add(offer)
            }
        }

        offerAdapter.updateOffers(offersResult)
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationRequestCode
            )
        } else {
            getLastKnownLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == locationRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            } else {
                // Permission refusée, gérez le cas ici
            }
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            launchServiceOffers("48.85", "2.34")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {


//                val latitude = location.latitude.toString()
//                val longitude = location.longitude.toString()

                // Je met les coordonnées de Montpellier car les coordonnées de l'émulateur sont biaisées
                val latitude = "43.61"
                val longitude = "3.87"

                launchServiceOffers(latitude, longitude)
            } else {
                // Gérez le cas où la localisation est null
            }
        }
    }


    fun launchServiceOffers(latitude: String, longitude: String) {

        val intent = Intent(requireContext(), OffresService::class.java).apply {
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }
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
    fun onGetOffersResult(event: OffersResultEvent) {
        offerAdapter.updateOffers(event.offres)
        listOffers = event.offres
    }
}