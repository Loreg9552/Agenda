package com.example.bachecaeventi.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bachecaeventi.data.model.Event
import com.example.bachecaeventi.ui.viewmodel.MainViewModel
import com.example.bachecaeventi.ui.components.AddEventCard
import com.example.bachecaeventi.ui.components.DayColumn
import com.example.bachecaeventi.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun BoardScreen(viewModel: MainViewModel) {

    val currentContext = LocalContext.current

    val importJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.importFromJson(currentContext, it) }
    }

    val exportJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportToJson(currentContext, it) }
    }

    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedEventId by remember { mutableStateOf<String?>(null) }

    val fourDays = remember(startDate) {
        (0..3).map { startDate.plusDays(it.toLong()) }
    }

    val italianLocale = Locale("it", "IT")
    val dayRangeText = remember(fourDays) {
        val startStr = "${fourDays.first().dayOfMonth} ${fourDays.first().month.getDisplayName(TextStyle.SHORT, italianLocale)}"
        val endStr = "${fourDays.last().dayOfMonth} ${fourDays.last().month.getDisplayName(TextStyle.SHORT, italianLocale)}"
        "$startStr — $endStr"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        TopHeaderBar(
            boardName = viewModel.boardName.ifBlank { "nome bacheca" },
            myName = viewModel.myName.ifBlank { "nome utente" },
            onImportJson = {
                importJsonLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
            },
            onExportJson = {
                exportJsonLauncher.launch("${viewModel.boardName.ifBlank { "bacheca" }}_eventi.json")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AddEventCard(onAddEvent = { title, date, timeStart, timeEnd, location, notes ->
            viewModel.addEvent(title, date, timeStart, timeEnd, location, notes)
        })

        Spacer(modifier = Modifier.height(20.dp))

        // === BARRA NAVIGAZIONE DATE OTTIMIZZATA PER SCHERMI PICCOLI ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Range date: usa weight(1f) per adattarsi senza spingere via i pulsanti
            Text(
                text = dayRangeText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PaperText,
                modifier = Modifier.weight(1f, fill = false),
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Pulsanti di navigazione compatti
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { startDate = LocalDate.now() },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PaperText)
                ) {
                    Text("Oggi", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { startDate = startDate.minusDays(4) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PaperText)
                ) {
                    Text("<", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { startDate = startDate.plusDays(4) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PaperText)
                ) {
                    Text(">", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === 4 COLONNE AFFIANCATE ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(540.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            fourDays.forEach { date ->
                val dateStringFormatted = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val dayEvents = viewModel.events.filter { it.date == dateStringFormatted }

                DayColumn(
                    dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, italianLocale).uppercase(),
                    dayNumber = date.dayOfMonth.toString(),
                    monthName = date.month.getDisplayName(TextStyle.SHORT, italianLocale).uppercase(),
                    events = dayEvents,
                    selectedEventId = selectedEventId,
                    myName = viewModel.myName,
                    onSelectEvent = { id ->
                        selectedEventId = if (selectedEventId == id) null else id
                    },
                    onToggleParticipant = { event, name -> viewModel.toggleParticipant(event, name) },
                    onRemoveParticipant = { event, name -> viewModel.removeParticipant(event, name) },
                    onAddBooker = { event, name -> viewModel.addBooker(event, name) },
                    onRemoveBooker = { event, name -> viewModel.removeBooker(event, name) },
                    onToggleBooked = { event -> viewModel.toggleBooked(event) },
                    onDeleteEvent = { id -> viewModel.deleteEvent(id) },
                    onEditEvent = { event: Event, title: String, time: String, timeEnd: String, dateStr: String, loc: String, notes: String ->
                        viewModel.updateEventDetails(event, title, time, timeEnd, dateStr, loc, notes)
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // === FOOTER ===
        Text(
            text = "BACHECA SEGNA EVENTI — coordinatevi, non doppiate le prenotazioni",
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = PaperDim.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun TopHeaderBar(
    boardName: String,
    myName: String,
    onImportJson: () -> Unit,
    onExportJson: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Riga 1: Nome Bacheca e Utente
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFF1E3A4A).copy(alpha = 0.6f),
                border = BorderStroke(1.dp, GoldAccent)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "BACHECA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PaperDim,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = boardName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAccent
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = "sei", fontSize = 12.sp, color = PaperDim)
                Text(text = myName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PaperText)
            }
        }

        // Riga 2: Pulsanti Import ed Export JSON su una riga dedicata sotto
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onImportJson,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, PaperDim.copy(alpha = 0.4f)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PaperText),
                modifier = Modifier.weight(1f)
            ) {
                Text("Importa JSON", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = onExportJson,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, PaperDim.copy(alpha = 0.4f)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PaperText),
                modifier = Modifier.weight(1f)
            ) {
                Text("Esporta JSON", fontSize = 12.sp)
            }
        }
    }
}