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
import com.example.gestioninterim.resultEvent.OffersResultEvent
import com.example.gestioninterim.services.OffresService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.Manifest;
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import com.example.gestioninterim.adapter.*
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.OfferDAO
import com.example.gestioninterim.services.InscriptionService
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.io.Serializable

class FragmentOffer : Fragment(), FilterDialogCallback {

    private lateinit var offerAdapter: OfferAdapter
    private lateinit var recyclerView: RecyclerView
    private val locationRequestCode = 1000
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var progressBar: ProgressBar
    private lateinit var listOffers: List<Offer>
    private lateinit var latitude : String
    private lateinit var longitude : String

    init {
        // Initialisation par défaut à Paris
        listOffers= emptyList()
        latitude = "48.85"
        longitude = "2.34"
    }

    private lateinit var inputMetier : TextInputEditText
    private var filterVille: String = ""
    private var filterDateDebut: String = ""
    private var filterDateFin: String = ""
    private var filterRayon: Int = 30



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_menu_top_search, container, false)

        // Initialisation des variables layout
        inputMetier = view.findViewById<TextInputEditText>(R.id.editMetier)
        val searchButton = view.findViewById<MaterialButton>(R.id.validateSearchJob)
        val moreFilter = view.findViewById<ImageButton>(R.id.imageButtonFiltres)

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


        // Listener du bouton de validation
        searchButton.setOnClickListener {
            if(inputMetier.text!!.isEmpty() && offerAdapter.getOffers().size != listOffers.size){
                offerAdapter.updateOffers(listOffers)
            }
            else{
                getOffersByJob(inputMetier.text.toString(), listOffers)
            }
        }

        // Listener du bouton d'ajout de filtres
        moreFilter.setOnClickListener{
            FilterPopup(requireContext(), this, filterVille, filterDateDebut, filterDateFin, filterRayon).show()
        }

        return view
    }

    // Fonction qui permet la sélection uniquement des offres correspondant au métier "job"
    private fun getOffersByJob(job : String, offerList : List<Offer>) {
        val offersResult = offerList.filter { offer -> offer.intitule.contains(job) }
        offerAdapter.updateOffers(offersResult)
    }


    // Fonction demandant la permission à l'utilisateur
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
                // Si l'utilisateur accepte la permission, je met les coordonnées de Montpellier
                getLastKnownLocation()
            } else {
                // Si la permission n'est pas accordée, je met les coordonnées de Paris
                latitude = "48.85"
                longitude = "2.34"
                launchServiceOffers(latitude, longitude)
            }
        }
    }

    private fun getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Les coordonnées de l'utilisateur réel
                latitude = "43.61"
                longitude = "3.87"
//                latitude = location.latitude.toString()
//                longitude = location.longitude.toString()

            } else {
                // Si la localisation est null, je met les coordonnées de Paris
                latitude = "48.85"
                longitude = "2.34"
            }
            launchServiceOffers(latitude, longitude)
        }
    }

    fun launchServiceOffers(latitude: String, longitude: String) {

        val offerRequest = OfferDAO(latitude = latitude, longitude = longitude)
        val intent = Intent(requireContext(), OffresService::class.java)
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
    fun onGetOffersResult(event: OffersResultEvent) {

        offerAdapter.updateOffers(event.offres)
        listOffers = event.offres
    }

    override fun onFiltersApplied(ville: String, dateDebut: String, dateFin: String, rayon: Int) {

        filterVille = ville
        filterDateDebut = dateDebut
        filterDateFin = dateFin
        filterRayon = rayon


        val offerRequest = OfferDAO(metier = inputMetier.text.toString(), ville = ville, latitude = latitude, longitude = longitude, rayon = rayon, dateDebut = dateDebut, dateFin = dateFin)
        val intent = Intent(requireContext(), OffresService::class.java)
        intent.putExtra("offerRequest", offerRequest as Serializable)
        requireContext().startService(intent)
    }
}