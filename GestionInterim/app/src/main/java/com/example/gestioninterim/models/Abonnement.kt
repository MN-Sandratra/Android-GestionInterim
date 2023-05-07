package com.example.gestioninterim.models

import java.io.Serializable

data class Abonnement(
    val _id: String,
    val type: String,
    val price: Int,
    val shortDescription: String,
    val description: String,
    val conditions: String,
    val cancellationConditions: String,
    val subscriptionName: String,
    val duration: Duration
) : Serializable

data class Duration(
    val value: Int,
    val unit: String
) : Serializable