package com.example.bachecaeventi.data.repository

import android.content.Context
import com.example.bachecaeventi.data.model.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalEventRepository(context: Context) {
    private val prefs = context.getSharedPreferences("bacheca_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getEvents(groupCode: String): List<Event> {
        val json = prefs.getString("events_$groupCode", null) ?: return emptyList()
        val type = object : TypeToken<List<Event>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveEvents(groupCode: String, events: List<Event>) {
        val json = gson.toJson(events)
        prefs.edit().putString("events_$groupCode", json).apply()
    }
}