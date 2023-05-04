package com.example.gestioninterim.adapter

interface FilterDialogCallback {
    fun onFiltersApplied(ville: String, dateDebut: String, dateFin: String, rayon: Int)
}