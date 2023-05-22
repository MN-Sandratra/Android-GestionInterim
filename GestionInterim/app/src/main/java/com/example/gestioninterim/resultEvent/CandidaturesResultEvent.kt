package com.example.gestioninterim.resultEvent

import com.example.gestioninterim.models.CandidatureEmployerResult
import com.example.gestioninterim.models.Offer
import com.example.gestioninterim.models.OfferResult

data class CandidaturesResultEvent(val candidatures: List<CandidatureEmployerResult>) {
}