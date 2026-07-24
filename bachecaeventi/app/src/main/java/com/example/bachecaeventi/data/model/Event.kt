package com.example.bachecaeventi.data.model

import java.util.UUID

data class Event(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var date: String, // Formato "YYYY-MM-DD"
    var time: String = "",
    var timeEnd: String = "",
    var loc: String = "",
    var notes: String = "",
    val participants: MutableList<String> = mutableListOf(),
    val bookedBy: MutableList<String> = mutableListOf(),
    var isBooked: Boolean = false
)