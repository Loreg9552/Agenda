package com.example.bachecaeventi.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bachecaeventi.ui.theme.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventCard(
    onAddEvent: (title: String, date: String, timeStart: String, timeEnd: String, location: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }

    // Data impostata di default su oggi (formato YYYY-MM-DD)
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    var timeStart by remember { mutableStateOf("") }
    var timeEnd by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // Formattatore per la visualizzazione nel campo (es. 21 Lug 2026)
    val displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Helper formattazione orario HH:mm
    fun formatTimeInput(input: String): String {
        val digitsOnly = input.filter { it.isDigit() }
        return when {
            digitsOnly.length <= 2 -> digitsOnly
            digitsOnly.length <= 4 -> "${digitsOnly.take(2)}:${digitsOnly.drop(2)}"
            else -> "${digitsOnly.take(2)}:${digitsOnly.substring(2, 4)}"
        }
    }

    // Titolo e Ora Inizio sono entrambi obbligatori per abilitare il tasto
    val isFormValid = title.isNotBlank() && timeStart.isNotBlank()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "AGGIUNGI NUOVO EVENTO",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. TITOLO (Obbligatorio)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titolo evento *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. SELEZIONE GIORNO / CALENDARIO
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedDate.format(displayDateFormatter),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Giorno") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleziona data",
                            tint = GoldAccent
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePickerDialog = true },
                    enabled = false, // Disabilitato il digit da tastiera per forzare il click sul calendario
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = GoldAccent
                    )
                )

                // Overlay invisibile per catturare il tap su tutta la casella
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePickerDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. ORARI (Ora inizio * Obbligatoria, Ora fine opzionale)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = timeStart,
                    onValueChange = { timeStart = formatTimeInput(it) },
                    label = { Text("Dalle (HH:mm) *") },
                    placeholder = { Text("13:00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = timeEnd,
                    onValueChange = { timeEnd = formatTimeInput(it) },
                    label = { Text("Alle (HH:mm)") },
                    placeholder = { Text("14:00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. LUOGO (Opzionale)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Luogo (opzionale)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5. NOTE (Opzionale)
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Note aggiuntive (opzionale)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // TASTO AGGIUNGI
            Button(
                onClick = {
                    if (isFormValid) {
                        onAddEvent(
                            title.trim(),
                            selectedDate.toString(), // Salva in formato YYYY-MM-DD
                            timeStart.trim(),
                            timeEnd.trim(),
                            location.trim(),
                            notes.trim()
                        )
                        // Reset form
                        title = ""
                        selectedDate = LocalDate.now()
                        timeStart = ""
                        timeEnd = ""
                        location = ""
                        notes = ""
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    disabledContainerColor = GoldAccent.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Aggiungi Evento",
                    color = if (isFormValid) NavyDeep else PaperDim,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // DIALOG CALENDARIO MATERIAL 3
    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                        }
                        showDatePickerDialog = false
                    }
                ) {
                    Text("OK", color = GoldAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Annulla", color = PaperDim)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun FieldLabel(label: String, optional: Boolean = false) {
    Row(modifier = Modifier.padding(bottom = 4.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GoldAccent,
            letterSpacing = 0.5.sp
        )
        if (optional) {
            Text(
                text = " (OPZIONALE)",
                fontSize = 11.sp,
                color = PaperDim.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = PaperDim.copy(alpha = 0.5f), fontSize = 13.sp) },
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = GoldAccent,
            unfocusedBorderColor = PaperDim.copy(alpha = 0.4f),
            focusedTextColor = PaperText,
            unfocusedTextColor = PaperText
        )
    )
}