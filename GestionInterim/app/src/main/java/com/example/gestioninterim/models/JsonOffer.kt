package com.example.gestioninterim.models

import java.io.Serializable
import java.util.*

data class JsonOffer(
    val intitule: String,
    val dateDebut: String,
    val dateFin: String,
    val experienceRequise: String,
    val description: String,
    val tauxHoraire: Double,
    val heureDebut: String,
    val disponibilite: Boolean,
    val etat: String,
    val ville: String,
    val adressePostale: String
) : Serializable

