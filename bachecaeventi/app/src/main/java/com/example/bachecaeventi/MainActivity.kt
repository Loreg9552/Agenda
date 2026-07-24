package com.example.bachecaeventi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.bachecaeventi.ui.screens.BoardScreen
import com.example.bachecaeventi.ui.screens.GateScreen
import com.example.bachecaeventi.ui.theme.*
import com.example.bachecaeventi.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current // ✅ Otteniamo il contesto Android corretto per Compose

            BachecaTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Bacheca Segna Eventi", color = PaperText) },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDeep),
                            // PULSANTE VISIBILE SOLO NELLA SECONDA PAGINA
                            navigationIcon = {
                                if (viewModel.isBoardVisible) {
                                    IconButton(onClick = { viewModel.goBack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Torna indietro",
                                            tint = GoldAccent
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (!viewModel.isBoardVisible) {
                            GateScreen(
                                onEnter = { group, name ->
                                    // Se il tuo ViewModel accetta il context passalo qui,
                                    // altrimenti puoi usare viewModel.enterGroup(group, name)
                                    viewModel.enterGroup(context, group, name)
                                    true
                                }
                            )
                        } else {
                            BoardScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}