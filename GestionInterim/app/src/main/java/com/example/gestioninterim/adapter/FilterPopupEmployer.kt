package com.example.gestioninterim.adapter

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.gestioninterim.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class FilterPopupEmployer(
    context: Context,
    private val callback: FilterDialogCallbackEmployer,
    private val filterMetier: String,
    private val filterDateDebut: String,
    private val filterDateFin: String
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_more_filters_offers_employers)

        val inputMetier = findViewById<TextInputEditText>(R.id.inputMetierFilter)
        val inputDateFinFilter = findViewById<TextInputEditText>(R.id.inputDateFinFilter)
        val inputDateDebutFilter = findViewById<TextInputEditText>(R.id.inputDateDebutFilter)
        val validateButton = findViewById<MaterialButton>(R.id.validateFilter)

        inputMetier.setText(filterMetier)
        inputDateDebutFilter.setText(filterDateDebut)
        inputDateFinFilter.setText(filterDateFin)

        inputDateFinFilter.setOnClickListener {
            showDatePicker(inputDateFinFilter)
        }

        inputDateDebutFilter.setOnClickListener {
            showDatePicker(inputDateDebutFilter)
        }

        validateButton.setOnClickListener {
            val metier = inputMetier.text.toString()
            val dateDebut = inputDateDebutFilter.text.toString()
            val dateFin = inputDateFinFilter.text.toString()

            callback.onFiltersApplied(metier, dateDebut, dateFin)
            dismiss()
        }

    }

    fun showDatePicker(textDate : TextInputEditText){
        // Créez une instance de DatePickerDialog
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // Mettez à jour l'entrée de texte avec la date sélectionnée
                textDate.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Affichez le DatePickerDialog
        datePickerDialog.show()
    }

}