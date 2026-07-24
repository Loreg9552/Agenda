package com.example.bachecaeventi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bachecaeventi.data.model.Event
import com.example.bachecaeventi.ui.theme.*

@Composable
fun DayColumn(
    dayName: String,
    dayNumber: String,
    monthName: String,
    events: List<Event>,
    selectedEventId: String?,
    myName: String,
    onSelectEvent: (String) -> Unit,
    onToggleParticipant: (Event, String) -> Unit,
    onRemoveParticipant: (Event, String) -> Unit,
    onAddBooker: (Event, String) -> Unit,
    onRemoveBooker: (Event, String) -> Unit,
    onToggleBooked: (Event) -> Unit,
    onEditEvent: (Event, String, String, String, String, String, String) -> Unit,
    onDeleteEvent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            // Intestazione giorno
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = dayName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                Text(text = dayNumber, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PaperText)
                Text(text = monthName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PaperDim)
            }

            HorizontalDivider(
                color = PaperDim.copy(alpha = 0.2f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Scroll verticale per evitare tagli
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                events.forEach { event ->
                    EventItemCard(
                        event = event,
                        isSelected = selectedEventId == event.id,
                        myName = myName,
                        onClick = { onSelectEvent(event.id) },
                        onToggleParticipant = { name -> onToggleParticipant(event, name) },
                        onRemoveParticipant = { name -> onRemoveParticipant(event, name) },
                        onAddBooker = { name -> onAddBooker(event, name) },
                        onRemoveBooker = { name -> onRemoveBooker(event, name) },
                        onToggleBooked = { onToggleBooked(event) },
                        onEditEvent = { title, time, timeEnd, dateStr, location, notes ->
                            onEditEvent(event, title, time, timeEnd, dateStr, location, notes)
                        },
                        onDelete = { onDeleteEvent(event.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EventItemCard(
    event: Event,
    isSelected: Boolean,
    myName: String,
    onClick: () -> Unit,
    onToggleParticipant: (String) -> Unit,
    onRemoveParticipant: (String) -> Unit,
    onAddBooker: (String) -> Unit,
    onRemoveBooker: (String) -> Unit,
    onToggleBooked: () -> Unit,
    onEditEvent: (String, String, String, String, String, String) -> Unit,
    onDelete: () -> Unit
) {
    var otherParticipantText by remember { mutableStateOf("") }
    var otherBookerText by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }

    val isBooked = event.isBooked || event.bookedBy.isNotEmpty()
    val greenAccent = Color(0xFF81C784)
    val darkPillBg = Color(0xFF1E3A4A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = NavyDeep)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${event.time}${if (event.timeEnd.isNotBlank()) " - ${event.timeEnd}" else ""}",
                    fontSize = 13.sp,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )

                if (isBooked) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(greenAccent)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = event.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = PaperText
            )

            // === AGGIUNTA CAMPI LUOGO E NOTE ===
            if (event.loc.isNotBlank() || event.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    if (event.loc.isNotBlank()) {
                        Text(
                            text = "📍 ${event.loc}",
                            fontSize = 12.sp,
                            color = GoldAccent,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                    if (event.notes.isNotBlank()) {
                        Text(
                            text = "📝 ${event.notes}",
                            fontSize = 12.sp,
                            color = PaperDim,
                            maxLines = 2
                        )
                    }
                }
            }

            // === CONTEGGIO PARTECIPANTI (RIMOSSO "PRENOTATO DA...") ===
            if (event.participants.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${event.participants.size} partecipanti",
                    fontSize = 12.sp,
                    color = PaperDim.copy(alpha = 0.8f)
                )
            }

            if (isSelected) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = PaperDim.copy(alpha = 0.15f), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // PARTECIPANTI
                Text("PARTECIPANTI", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = PaperDim, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    event.participants.forEach { name ->
                        PillChip(name = "$name ×", onClick = { onRemoveParticipant(name) })
                    }
                    PillChip(name = "+ io", onClick = { onToggleParticipant(myName.ifBlank { "io" }) })
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MiniInputField(
                        value = otherParticipantText,
                        onValueChange = { otherParticipantText = it },
                        placeholder = "aggiungi...",
                        modifier = Modifier.width(110.dp)
                    )
                    CircleAddButton(onClick = {
                        if (otherParticipantText.isNotBlank()) {
                            onToggleParticipant(otherParticipantText)
                            otherParticipantText = ""
                        }
                    })
                }

                Spacer(modifier = Modifier.height(12.dp))

                // CHI PRENOTA
                Text("CHI PRENOTA", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = PaperDim, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    event.bookedBy.forEach { name ->
                        PillChip(name = "$name ×", isOutlined = true, onClick = { onRemoveBooker(name) })
                    }
                    PillChip(name = "+ io", onClick = { onAddBooker(myName.ifBlank { "io" }) })
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MiniInputField(
                        value = otherBookerText,
                        onValueChange = { otherBookerText = it },
                        placeholder = "aggiungi...",
                        modifier = Modifier.width(110.dp)
                    )
                    CircleAddButton(onClick = {
                        if (otherBookerText.isNotBlank()) {
                            onAddBooker(otherBookerText)
                            otherBookerText = ""
                        }
                    })
                }

                Spacer(modifier = Modifier.height(12.dp))

                // TOGGLE PRENOTATO
                Surface(
                    onClick = onToggleBooked,
                    shape = RoundedCornerShape(20.dp),
                    color = if (isBooked) greenAccent.copy(alpha = 0.85f) else darkPillBg,
                    contentColor = if (isBooked) NavyDeep else PaperText,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isBooked) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                        Text(
                            text = if (isBooked) "Prenotato" else "Segna prenotato",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // BOTTONI MODIFICA ED ELIMINA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 6.dp),
                        border = BorderStroke(1.dp, PaperDim.copy(alpha = 0.3f))
                    ) {
                        Text("Modifica", fontSize = 12.sp, color = PaperText)
                    }

                    OutlinedButton(
                        onClick = onDelete,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.weight(1.2f),
                        contentPadding = PaddingValues(vertical = 6.dp),
                        border = BorderStroke(1.dp, PaperDim.copy(alpha = 0.3f))
                    ) {
                        Text("Elimina evento", fontSize = 12.sp, color = PaperText)
                    }
                }
            }
        }
    }

    // DIALOG MODIFICA EVENTO
    if (showEditDialog) {
        var editTitle by remember { mutableStateOf(event.title) }
        var editTime by remember { mutableStateOf(event.time) }
        var editTimeEnd by remember { mutableStateOf(event.timeEnd) }
        var editDate by remember { mutableStateOf(event.date) }
        var editLocation by remember { mutableStateOf(event.loc) }
        var editNotes by remember { mutableStateOf(event.notes) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Modifica Evento", color = PaperText) },
            containerColor = NavyCard,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Titolo", color = PaperDim) },
                        textStyle = TextStyle(color = PaperText)
                    )
                    OutlinedTextField(
                        value = editDate,
                        onValueChange = { editDate = it },
                        label = { Text("Data (YYYY-MM-DD)", color = PaperDim) },
                        textStyle = TextStyle(color = PaperText)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editTime,
                            onValueChange = { editTime = it },
                            label = { Text("Ora Inizio", color = PaperDim) },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = PaperText)
                        )
                        OutlinedTextField(
                            value = editTimeEnd,
                            onValueChange = { editTimeEnd = it },
                            label = { Text("Ora Fine", color = PaperDim) },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = PaperText)
                        )
                    }
                    OutlinedTextField(
                        value = editLocation,
                        onValueChange = { editLocation = it },
                        label = { Text("Luogo", color = PaperDim) },
                        textStyle = TextStyle(color = PaperText)
                    )
                    OutlinedTextField(
                        value = editNotes,
                        onValueChange = { editNotes = it },
                        label = { Text("Note", color = PaperDim) },
                        textStyle = TextStyle(color = PaperText)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onEditEvent(editTitle, editTime, editTimeEnd, editDate, editLocation, editNotes)
                    showEditDialog = false
                }) {
                    Text("Salva")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Annulla", color = PaperDim)
                }
            }
        )
    }
}

@Composable
private fun PillChip(
    name: String,
    isOutlined: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isOutlined) Color.Transparent else Color(0xFF1E3A4A),
        border = if (isOutlined) BorderStroke(1.dp, Color(0xFF81C784)) else null,
        contentColor = PaperText
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun MiniInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E3A4A))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                fontSize = 12.sp,
                color = PaperDim.copy(alpha = 0.5f)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = PaperText,
                fontSize = 12.sp
            ),
            cursorBrush = SolidColor(GoldAccent),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CircleAddButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E3A4A))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text("+", color = PaperText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}