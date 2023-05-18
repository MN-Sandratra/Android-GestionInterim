package com.example.gestioninterim.utilisateurEmployeur

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gestioninterim.R
import com.example.gestioninterim.resultEvent.AbonnementsResultEvent
import com.example.gestioninterim.services.AbonnementsService
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*

class FragmentAbonnementPaiement(private val prixAbonnement: Int, private val typeAbonnement: String) : Fragment() {

    private lateinit var inputMailEditText : TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_paiement_abonnement, container, false)

        val abonnementChoisiTextView: TextView = view.findViewById(R.id.chooseAbonnementText)
        val prixAbonnementTextView: TextView = view.findViewById(R.id.prixAbonnementChoisi)
        val abonnementBoutonValidation: MaterialButton = view.findViewById(R.id.validationPaiementButton)
        inputMailEditText = view.findViewById(R.id.inputTextContact)

        val inputTextDateExpiration = view.findViewById<TextInputEditText>(R.id.inputTextDateExpiration)
        inputTextDateExpiration.setOnClickListener {
            val now = Calendar.getInstance()
            val dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                { view, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, monthOfYear)
                    val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())
                    inputTextDateExpiration.setText(dateFormat.format(selectedDate.time))
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.version = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.Version.VERSION_2
            dpd.showYearPickerFirst(true)
            dpd.setYearRange(now.get(Calendar.YEAR), now.get(Calendar.YEAR) + 20)
            dpd.setTitle("Select Month and Year")
            dpd.accentColor = Color.parseColor("#FF4081")
            dpd.show(parentFragmentManager, "DatePickerDialog")
        }


        abonnementChoisiTextView.text = resources.getString(R.string.abonnementChoisi) + " " + typeAbonnement
        prixAbonnementTextView.text = prixAbonnement.toString() + ".00€"

        abonnementBoutonValidation.setOnClickListener {
            launchServiceAbonnementPaiement()
        }

        return view
    }

    fun launchServiceAbonnementPaiement() {
        println("JE SUIS LAA")
        val intent = Intent(requireContext(), AbonnementsService::class.java)
        intent.putExtra("email", inputMailEditText.text.toString())
        intent.putExtra("type", typeAbonnement)
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
    fun onPostAbonnementsResult(event: AbonnementsResultEvent) {
        val validation = event.validation
        println("JE SUIS LA 4")
        println(validation)
        if(validation == true){
            println("JE SUIS LA 5")
            val intent = Intent(requireContext(), MainEmployeurActivity::class.java)
            requireActivity().startActivity(intent)
        }
    }
}