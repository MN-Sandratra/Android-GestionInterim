package com.example.gestioninterim.models

import java.util.Date

class UtilisateurInterimaire(
    nom: String,
    prenom: String,
    password: String,
    mail : String? = null,
    telephone : String? = null,
    dateNaissance : Date? = null,
    ville : String? = null,
    nationnalite : String? = null,
    uriCV : String? = null,
    commentaires : String? = null
) : Utilisateur
