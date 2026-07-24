package com.example.bachecaeventi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bachecaeventi.data.model.Event
import com.example.bachecaeventi.ui.theme.*

@Composable
fun EventCard(
    event: Event,
    myName: String,
    onToggleParticipant: (String) -> Unit,
    onToggleBooked: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (event.time.isNotBlank()) "${event.time} - ${event.title}" else event.title,
                    fontWeight = FontWeight.Bold,
                    color = PaperText,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (event.isBooked) SageGreen else CoralAccent)
                )
            }

            if (event.loc.isNotBlank()) {
                Text("📍 ${event.loc}", color = PaperDim, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sezione Partecipanti
            Text("PARTECIPANTI:", fontSize = 11.sp, color = PaperDim, fontWeight = FontWeight.Bold)
            Text(
                text = if (event.participants.isEmpty()) "Nessuno ancora" else event.participants.joinToString(", "),
                color = GoldAccent,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Azioni
            Row(verticalAlignment = Alignment.CenterVertically) {
                val isParticipating = event.participants.contains(myName)
                OutlinedButton(
                    onClick = { onToggleParticipant(myName) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isParticipating) GoldAccent else PaperDim
                    )
                ) {
                    Text(if (isParticipating) "✓ Ci sono" else "+ Partecipo")
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete) {
                    Text("🗑️", fontSize = 14.sp)
                }
            }
        }
    }
}