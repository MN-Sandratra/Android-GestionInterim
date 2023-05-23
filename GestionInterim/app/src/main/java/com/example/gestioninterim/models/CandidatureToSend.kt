package com.example.gestioninterim.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class CandidatureToSend (
    @Expose var email : String?,
    @Expose val firstName: String,
    @Expose val lastName: String,
    @Expose val nationality: String? = null,
    @Expose val dateOfBirth : String? = null,
    @Expose val contenuCv : ByteArray? = null,
    @Expose val cv : String? = null,
    @Expose val contenuLm : ByteArray? = null,
    @Expose val lm : String? = null,
) : Serializable
