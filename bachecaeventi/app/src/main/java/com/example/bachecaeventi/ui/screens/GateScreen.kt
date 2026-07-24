package com.example.bachecaeventi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bachecaeventi.ui.theme.*

@Composable
fun GateScreen(onEnter: (String, String) -> Boolean) {
    var groupInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("BACHECA CONDIVISA", color = GoldAccent, fontSize = 12.sp, letterSpacing = 2.sp)
        Text("Bacheca Segna Eventi", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PaperText)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = NavyCard),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = groupInput,
                    onValueChange = { groupInput = it },
                    label = { Text("Codice del gruppo (es. vacanze26)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Il tuo nome") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        "Inserisci sia il codice che il nome.",
                        color = CoralAccent,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (!onEnter(groupInput, nameInput)) {
                            showError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Entra nella bacheca", color = NavyDeep, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}