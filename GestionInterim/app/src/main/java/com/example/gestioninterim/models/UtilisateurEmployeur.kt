package com.example.gestioninterim.models

import java.io.Serializable

data class UtilisateurEmployeur (
    val companyName : String,
    val department : String?,
    val subDepartment : String?,
    val nationalNumber : String,
    val contactPerson1 : String?,
    val contactPerson2 : String?,
    val email1 : String,
    val password : String,
    val email2 : String?,
    val phone1 : String?,
    val phone2 : String?,
    val address : String,
    val website : String?,
    val linkedin : String?,
    val facebook : String?
) : Serializable, Utilisateur