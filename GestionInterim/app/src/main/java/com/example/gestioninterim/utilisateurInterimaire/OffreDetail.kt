package com.example.gestioninterim.utilisateurInterimaire

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gestioninterim.BuildConfig
import com.example.gestioninterim.R
import com.example.gestioninterim.message.MessageActivity
import com.example.gestioninterim.models.OfferResult
import com.example.gestioninterim.models.UtilisateurInterimaire
import com.example.gestioninterim.utilisateurInterimaire.FragmentOfferDetails.Companion.ARG_OFFER
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection


class OffreDetail : Fragment() {

    private lateinit var offer: OfferResult
    private lateinit var map: MapView
    private lateinit var user : UtilisateurInterimaire
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_offre_detail_offre, container, false)

        val gson = Gson()
        val offerJson = arguments?.getString(ARG_OFFER)
        offer = gson.fromJson(offerJson, OfferResult::class.java)

        val sharedPreferences = activity?.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        val gsons = Gson()
        val jsonUser = sharedPreferences!!.getString("user", "")
        user = gsons.fromJson(jsonUser, UtilisateurInterimaire::class.java)

        val hostnameVerifier = HostnameVerifier { _, _ -> true }
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)

        val ctx = activity?.applicationContext
        Configuration.getInstance().load(ctx, ctx?.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        map = view.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        map.setMultiTouchControls(true)

        val mapController = map.controller
        mapController.setZoom(15.0)

        val geoPoint = GeoPoint(offer.latitude, offer.longitude)
        mapController.setCenter(geoPoint)

        val marker = Marker(map)
        marker.position = geoPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)


        Log.d("Affichage details", "===> $offer")

        val employeurTextView = view.findViewById<TextView>(R.id.employeurTextView)
        val intituleTextView = view.findViewById<TextView>(R.id.intituleTextView)
        val villeTextView = view.findViewById<TextView>(R.id.villeTextView)
        val adresseTextView = view.findViewById<TextView>(R.id.adresseTextView)
        val dateDebutTextView = view.findViewById<TextView>(R.id.dateDebutTextView)
        val dateFinTextView = view.findViewById<TextView>(R.id.dateFinTextView)
        val experienceTextView = view.findViewById<TextView>(R.id.experienceTextView)
        val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)
        val tauxHoraireTextView = view.findViewById<TextView>(R.id.tauxHoraireTextView)

        val date = Date()
        val sdf = SimpleDateFormat("dd/MM/yyyy")

        employeurTextView.text = offer.employeur
        intituleTextView.text = offer.intitule
        villeTextView.text = offer.ville
        adresseTextView.text = offer.adressePostale
        dateDebutTextView.text = sdf.format(offer.dateDebut).toString()
        dateFinTextView.text = sdf.format(offer.dateFin).toString()
        experienceTextView.text = offer.experienceRequise
        descriptionTextView.text = offer.description
        tauxHoraireTextView.text = offer.tauxHoraire.toString()

        val parentActivity = activity as MainInterimaireActivity
        val navigationView = parentActivity.findViewById<BottomNavigationView>(R.id.navigation_interimaire)

        val messageButton:Button=view.findViewById(R.id.messageButton)
        messageButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("participantId", offer.id_posteur)
            intent.putExtra("participantName",offer.employeur)
            intent.putExtra("userId",user._id)
            context?.startActivity(intent)
        })
        return view
    }
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()  // ajoutez cette ligne
    }
    companion object {
        fun newInstance(offer: OfferResult): OffreDetail {
            val fragment = OffreDetail()
            val args = Bundle()
            args.putSerializable("offer", offer)
            fragment.arguments = args
            return fragment
        }
    }
}
