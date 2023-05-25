package com.example.gestioninterim.resultEvent

import com.example.gestioninterim.models.Candidature
import com.example.gestioninterim.models.CandidatureEmployerResult
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.OfferResult

data class CandidaturesResultInterimaireEvent(val candidatures: List<Candidature>) {
}