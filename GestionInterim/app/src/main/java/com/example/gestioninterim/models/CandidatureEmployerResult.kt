package com.example.gestioninterim.models

import java.io.Serializable
import java.util.*

data class CandidatureEmployerResult (
    val candidature : Candidature,
    val offre: OfferResult
) : Serializable
