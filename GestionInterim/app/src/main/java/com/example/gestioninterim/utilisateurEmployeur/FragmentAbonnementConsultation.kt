package com.example.gestioninterim.utilisateurEmployeur

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestioninterim.R
import com.example.gestioninterim.adapter.*
import com.example.gestioninterim.models.Abonnement
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.OfferDAO
import com.example.gestioninterim.resultEvent.AbonnementsResultEvent
import com.example.gestioninterim.resultEvent.OffersResultEvent
import com.example.gestioninterim.services.AbonnementsService
import com.example.gestioninterim.services.OffresService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.Serializable

class FragmentAbonnementConsultation : Fragment(){

    private lateinit var abonnementAdapter: AbonnementAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listAbonnements : List<Abonnement>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_consult_abonnement, container, false)

        // Initialise le RecyclerView et l'adaptateur.
        recyclerView = view.findViewById(R.id.vertical_recycler_view_abonnements)

        abonnementAdapter = AbonnementAdapter(activity as OnAbonnementClickListener)


        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = abonnementAdapter
        }

        recyclerView.addItemDecoration(AbonnementDecorationItem())


        launchServiceAbonnements()

        return view


    }

    fun launchServiceAbonnements() {
        val intent = Intent(requireContext(), AbonnementsService::class.java)
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
    fun onGetAbonnementsResult(event: AbonnementsResultEvent) {

        abonnementAdapter.updateAbonnements(event.abonnements!!)
        listAbonnements = event.abonnements
    }

}