package com.example.gestioninterim.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Candidature (
    @SerializedName("_id")
    val identifiant : String?,
    val firstName : String,
    val lastName : String,
    val nationality : String,
    val dateOfBirth : String,
    val cv : String,
    val lm : String?,
    val email : String? = null,
    val phoneNumber : String? = null
) : Serializable
