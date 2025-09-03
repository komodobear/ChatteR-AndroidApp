package com.example.chater.Data

data class Message(
    val senderFirstName: String = "",
    val senderId:String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
