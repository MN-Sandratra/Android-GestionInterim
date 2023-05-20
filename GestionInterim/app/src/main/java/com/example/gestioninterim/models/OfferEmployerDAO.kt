package com.example.gestioninterim.models

import java.io.Serializable

data class OfferEmployerDAO(
    val email: String? = null,
    val metier: String? = null,
    val dateDebut: String? = null,
    val dateFin: String? = null
) : Serializable