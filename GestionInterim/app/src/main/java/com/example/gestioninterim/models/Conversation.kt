package com.example.gestioninterim.models

import java.io.Serializable

data class Conversation (
    val participant:Participant,
    val lastMessage:String,
    val timestamp:String
)

data class Participant(
    val id:String,
    val name:String,
): Serializable