package com.example.gestioninterim.models


data class ListConversationDao(
    val participantId:String,
    val userName: String,
    val lastMessage: String,
    val date:String)