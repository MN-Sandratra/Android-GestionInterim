package com.example.gestioninterim.resultEvent

import com.example.gestioninterim.models.Abonnement

data class AbonnementsResultEvent(val abonnements: List<Abonnement>? = null, val validation : Boolean? = null) {
}