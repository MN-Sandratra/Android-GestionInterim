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

class FilterPopup(context : Context, private val callback: FilterDialogCallback) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_more_filters_offer)

        val inputVille = findViewById<TextInputEditText>(R.id.inputVilleFilter)
        val inputDateFinFilter = findViewById<TextInputEditText>(R.id.inputDateFinFilter)
        val inputDateDebutFilter = findViewById<TextInputEditText>(R.id.inputDateDebutFilter)
        val validateButton = findViewById<MaterialButton>(R.id.validateFilter)
        val slider = findViewById<Slider>(R.id.slider)
        val textSlider = findViewById<TextView>(R.id.sliderRayonText)


        slider.addOnChangeListener { slider, value, fromUser ->
            textSlider.text = "Dans un rayon autour de : ${value.toInt()} km"
        }

        inputDateFinFilter.setOnClickListener {
            showDatePicker(inputDateFinFilter)
        }

        inputDateDebutFilter.setOnClickListener {
            showDatePicker(inputDateDebutFilter)
        }

        validateButton.setOnClickListener {
            val ville = inputVille.text.toString()
            val dateDebut = inputDateDebutFilter.text.toString()
            val dateFin = inputDateFinFilter.text.toString()
            val rayon = slider.value.toInt()

            callback.onFiltersApplied(ville, dateDebut, dateFin, rayon)
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