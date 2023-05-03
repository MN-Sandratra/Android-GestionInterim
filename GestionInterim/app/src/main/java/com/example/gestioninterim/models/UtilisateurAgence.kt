package com.example.gestioninterim.models

import java.io.Serializable
import java.util.*

data class UtilisateurAgence (
    val nom: String,
) : Serializable, Utilisateur
