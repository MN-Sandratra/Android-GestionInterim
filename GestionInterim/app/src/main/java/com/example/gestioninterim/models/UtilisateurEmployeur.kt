package com.example.gestioninterim.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class UtilisateurEmployeur (
    @Expose val companyName : String,
    @Expose val department : String?,
    @Expose val subDepartment : String?,
    @Expose val nationalNumber : String,
    @Expose val contactPerson1 : String?,
    @Expose val contactPerson2 : String?,
    @Expose val email1 : String,
    @Expose val password : String,
    @Expose val email2 : String?,
    @Expose val phone1 : String?,
    @Expose val phone2 : String?,
    @Expose val address : String,
    @Expose val ville : String,
    @Expose val website : String?,
    @Expose val linkedin : String?,
    @Expose val facebook : String?
) : Serializable, Utilisateur