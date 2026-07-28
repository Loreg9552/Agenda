package com.example.bachecaeventi.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.bachecaeventi.data.model.Event
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LocalEventRepository {

    private const val PREF_NAME = "bacheca_eventi_prefs"
    private const val KEY_LAST_BOARD = "last_board_name"
    private const val KEY_LAST_USER = "last_user_name"
    private const val PREFIX_EVENTS = "events_board_"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // SALVATAGGIO / CARICAMENTO SESSIONE UTENTE
    fun saveLastSession(context: Context, boardName: String, userName: String) {
        getPreferences(context).edit().apply {
            putString(KEY_LAST_BOARD, boardName)
            putString(KEY_LAST_USER, userName)
            apply()
        }
    }

    // === SALVATAGGIO / CARICAMENTO EVENTI BACHECA
    fun saveEvents(context: Context, boardName: String, events: List<Event>) {
        if (boardName.isBlank()) return
        val jsonString = Gson().toJson(events)
        getPreferences(context).edit().apply {
            putString(PREFIX_EVENTS + boardName.trim().lowercase(), jsonString)
            apply()
        }
    }

    fun loadEvents(context: Context, boardName: String): List<Event> {
        if (boardName.isBlank()) return emptyList()
        val jsonString = getPreferences(context).getString(PREFIX_EVENTS + boardName.trim().lowercase(), null)
            ?: return emptyList()

        return try {
            val type = object : TypeToken<List<Event>>() {}.type
            Gson().fromJson(jsonString, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}