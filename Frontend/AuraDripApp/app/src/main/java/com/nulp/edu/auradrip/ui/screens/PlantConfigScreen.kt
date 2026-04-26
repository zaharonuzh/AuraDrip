package com.nulp.edu.auradrip.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nulp.edu.auradrip.AuraDripApplication
import com.nulp.edu.auradrip.R
import com.nulp.edu.auradrip.ui.viewmodel.PlantConfigViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantConfigScreen(
    navController: NavController,
    plantId: Int
) {
    val context = LocalContext.current
    val application = context.applicationContext as AuraDripApplication
    
    val viewModel: PlantConfigViewModel = viewModel(
        factory = PlantConfigViewModel.provideFactory(application.plantRepository, plantId)
    )

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val successMessage = stringResource(R.string.save_success)

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            if (event == "save_success") {
                snackbarHostState.showSnackbar(successMessage)
                navController.popBackStack()
            } else {
                snackbarHostState.showSnackbar(event)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.plant_settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Plant Name (Read-Only)
                OutlinedTextField(
                    value = uiState.plantName,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.plant_name)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Operating Mode Dropdown
                var expanded by remember { mutableStateOf(false) }
                val modes = listOf(
                    1 to stringResource(R.string.smart_mode),
                    2 to stringResource(R.string.manual_mode),
                    3 to stringResource(R.string.fixed_threshold_mode)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = modes.find { it.first == uiState.controlMode }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mode") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true).fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        modes.forEach { (id, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    viewModel.updateOperatingMode(id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (uiState.controlMode == 3) {
                    OutlinedTextField(
                        value = uiState.manualThreshold,
                        onValueChange = { viewModel.updateThreshold(it) },
                        label = { Text("Threshold (0-100%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.saveConfig() },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(stringResource(R.string.save_button))
                }
            }
        }
    }
}
