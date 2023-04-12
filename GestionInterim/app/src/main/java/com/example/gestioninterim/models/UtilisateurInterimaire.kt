package com.example.gestioninterim.models

import java.io.Serializable
import java.util.Date

data class UtilisateurInterimaire(
    val firstName: String,
    val lastName: String,
    val nationality: String? = null,
    val dateOfBirth : String? = null,
    val phoneNumber : String? = null,
    val email : String? = null,
    val city : String? = null,
    val cv : String? = null,
    val comments : String? = null,
    val password : String
) : Serializable
