package com.example.gestioninterim.models

import java.time.LocalTime
import java.util.Date

class Offre(val lieu:String, val tarifHoraire: Float, val description: String, val dateDebut: Date, val dateFin:Date, val heureDebut: LocalTime, val heureFin:LocalTime)

{
}