package com.nulp.edu.auradrip.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nulp.edu.auradrip.AuraDripApplication
import com.nulp.edu.auradrip.R
import com.nulp.edu.auradrip.domain.model.PlantStatus
import com.nulp.edu.auradrip.ui.viewmodel.PlantUiState
import com.nulp.edu.auradrip.ui.viewmodel.PlantViewModel
import com.nulp.edu.auradrip.utils.toTimeAgo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val application = context.applicationContext as AuraDripApplication
    val plantViewModel: PlantViewModel = viewModel(
        factory = PlantViewModel.provideFactory(application.plantRepository)
    )

    val uiState by plantViewModel.uiState.collectAsState()
    val isRefreshing by plantViewModel.isRefreshing.collectAsState()

    // We'll use a standard Scaffold to host the content
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard), fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        // Since PullToRefreshBox is in Material3 1.3.0+, let's see if it works. 
        // If not, we'll fall back to something else. We'll try the new PullToRefreshBox.
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { plantViewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is PlantUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is PlantUiState.Success -> {
                    PlantStatusContent(plant = state.data)
                }
                is PlantUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.error_message, state.message),
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { plantViewModel.refresh() }) {
                                Text(stringResource(R.string.try_again))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlantStatusContent(plant: PlantStatus) {
    val resources = LocalContext.current.resources

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatusCard(
            title = stringResource(R.string.moisture),
            value = stringResource(R.string.moisture_value, plant.currentMoisture.toInt())
        )
        StatusCard(
            title = stringResource(R.string.temperature),
            value = stringResource(R.string.temperature_value, plant.currentTemp)
        )
        StatusCard(
            title = stringResource(R.string.age),
            value = resources.getQuantityString(R.plurals.plant_age, plant.ageDays, plant.ageDays)
        )
        
        Text(
            text = stringResource(R.string.updated_time, plant.lastUpdate.toTimeAgo()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun StatusCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
