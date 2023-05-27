package com.example.gestioninterim.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class OfferResult (
    @SerializedName("_id")
    val identifiant: String,
    val id_posteur:String,
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
    val adressePostale: String,
    val latitude: Double,
    val longitude: Double
) : Serializable
