package com.example.gestioninterim.models

import java.io.Serializable

data class Message(
    val sender: String,
    val receiver: String,
    val content: String,
    val timestamp: String
): Serializable
