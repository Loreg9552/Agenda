package com.example.bachecaeventi.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bachecaeventi.data.model.Event
import com.example.bachecaeventi.data.repository.LocalEventRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainViewModel : ViewModel() {

    // 🎯 STATO DI VISIBILITÀ DELLA BACHECA
    var isBoardVisible by mutableStateOf(false)
        private set

    var boardName: String by mutableStateOf("")
        private set

    var myName: String by mutableStateOf("")
        private set

    val events = mutableStateListOf<Event>()

    // 🎯 METODO PER ENTRARE/INIZIALIZZARE LA BACHECA
    fun initBoard(context: Context, board: String, name: String) {
        this.boardName = board.trim().lowercase()
        this.myName = name.trim()

        LocalEventRepository.saveLastSession(context, boardName, myName)
        loadEvents(context)

        isBoardVisible = true
    }

    fun enterGroup(context: Context, board: String, name: String) {
        initBoard(context, board, name)
    }

    // 🎯 METODO PER TORNARE INDIETRO
    fun goBack() {
        isBoardVisible = false
    }

    // Carica gli eventi salvati localmente
    fun loadEvents(context: Context) {
        if (boardName.isBlank()) return
        val savedList = LocalEventRepository.loadEvents(context, boardName)
        events.clear()
        events.addAll(savedList)
    }

    // 🎯 AGGIUNTA ED EDIT EVENTI
    fun addEvent(
        title: String,
        date: String,
        timeStart: String,
        timeEnd: String,
        location: String,
        notes: String
    ) {
        val newEvent = Event(
            id = UUID.randomUUID().toString(),
            title = title,
            date = date,
            time = timeStart,
            timeEnd = timeEnd,
            loc = location,
            notes = notes,
            participants = mutableListOf(),
            bookedBy = mutableListOf(),
            isBooked = false
        )
        events.add(newEvent)
    }

    fun updateEventDetails(
        event: Event,
        title: String,
        timeStart: String,
        timeEnd: String,
        dateStr: String,
        location: String,
        notes: String
    ) {
        val index = events.indexOfFirst { it.id == event.id }
        if (index != -1) {
            events[index] = event.copy(
                title = title,
                time = timeStart,
                timeEnd = timeEnd,
                date = dateStr,
                loc = location,
                notes = notes
            )
        }
    }

    fun deleteEvent(eventId: String) {
        events.removeAll { it.id == eventId }
    }

    // 🎯 PARTECIPANTI E PRENOTAZIONI
    fun toggleParticipant(event: Event, name: String) {
        if (event.participants.contains(name)) {
            event.participants.remove(name)
        } else {
            event.participants.add(name)
        }
    }

    fun removeParticipant(event: Event, name: String) {
        event.participants.remove(name)
    }

    fun addBooker(event: Event, name: String) {
        val index = events.indexOfFirst { it.id == event.id }
        if (index != -1 && !event.bookedBy.contains(name)) {
            val updatedBookers = event.bookedBy.toMutableList().apply { add(name) }
            events[index] = event.copy(isBooked = true, bookedBy = updatedBookers)
        }
    }

    fun removeBooker(event: Event, name: String) {
        event.bookedBy.remove(name)
        if (event.bookedBy.isEmpty()) {
            event.isBooked = false
        }
    }

    fun toggleBooked(event: Event) {
        event.isBooked = !event.isBooked
        if (event.isBooked && !event.bookedBy.contains(myName) && myName.isNotBlank()) {
            event.bookedBy.add(myName)
        } else if (!event.isBooked) {
            event.bookedBy.clear()
        }
    }

    // 🎯 ESPORTA IN JSON
    fun exportToJson(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentList = events.toList()
                val prettyGson = Gson().newBuilder().setPrettyPrinting().create()
                val jsonString = prettyGson.toJson(currentList)

                context.contentResolver.openOutputStream(uri)?.use { stream ->
                    stream.write(jsonString.toByteArray(Charsets.UTF_8))
                    stream.flush()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 🎯 IMPORTA DA JSON
    fun importFromJson(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader(Charsets.UTF_8).readText()
                }

                if (!jsonString.isNullOrBlank()) {
                    val type = object : TypeToken<ArrayList<Event>>() {}.type
                    val importedEvents: ArrayList<Event>? = Gson().fromJson(jsonString, type)

                    if (!importedEvents.isNullOrEmpty()) {
                        withContext(Dispatchers.Main) {
                            events.clear()
                            events.addAll(importedEvents)
                            persistEvents(context)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun persistEvents(context: Context) {
        LocalEventRepository.saveEvents(context, boardName, events.toList())
    }
}