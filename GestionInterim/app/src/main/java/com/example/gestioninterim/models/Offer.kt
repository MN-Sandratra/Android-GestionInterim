package com.example.gestioninterim.models

import java.io.Serializable
import java.util.*

data class Offer (
    val employeur: String,
    val intitule: String,
    val dateDebut: Date,
    val dateFin: Date,
    val experienceRequise: Int,
    val description: String,
    val tauxHoraire: Double,
    val disponibilite: Boolean,
    val etat: String,
    val remuneration: Double,
    val ville: String,
    val adressePostale: String
) : Serializable
