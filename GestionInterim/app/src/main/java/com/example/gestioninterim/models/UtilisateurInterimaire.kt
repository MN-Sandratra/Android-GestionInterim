package com.example.gestioninterim.models

import com.google.gson.annotations.Expose
import java.io.InputStream
import java.io.Serializable
import java.util.Date

data class UtilisateurInterimaire(
    @Expose val _id:String,
    @Expose val firstName: String,
    @Expose val lastName: String,
    @Expose val nationality: String? = null,
    @Expose val dateOfBirth : String? = null,
    @Expose val phoneNumber : String? = null,
    @Expose val email : String? = null,
    @Expose val city : String? = null,
    @Expose val contenuCv : ByteArray? = null,
    @Expose val cv : String? = null,
    @Expose val comments : String? = null,
    @Expose val password : String
) : Serializable, Utilisateur
