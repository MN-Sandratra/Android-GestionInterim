package com.example.gestioninterim.models

import java.io.Serializable
import java.util.*

data class Offer (
    val employeur: String,
    val intitule: String,
    val dateDebut: Date,
    val dateFin: Date,
    val experienceRequise: String,
    val description: String,
    val tauxHoraire: Double,
    val heureDebut: String,
    val disponibilite: Boolean,
    val etat: String,
    val ville: String,
    val adressePostale: String
) : Serializable
