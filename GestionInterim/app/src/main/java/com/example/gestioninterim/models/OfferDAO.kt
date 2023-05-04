package com.example.gestioninterim.models

import java.io.Serializable

data class OfferDAO(
    val metier: String? = null,
    val ville: String? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val rayon: Int = 30,
    val dateDebut: String? = null,
    val dateFin: String? = null
) : Serializable