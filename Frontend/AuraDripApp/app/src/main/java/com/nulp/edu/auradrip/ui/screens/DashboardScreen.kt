package com.nulp.edu.auradrip.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nulp.edu.auradrip.AuraDripApplication
import com.nulp.edu.auradrip.R
import com.nulp.edu.auradrip.domain.model.PlantConfig
import com.nulp.edu.auradrip.domain.model.PlantStatus
import com.nulp.edu.auradrip.ui.viewmodel.PlantUiState
import com.nulp.edu.auradrip.ui.viewmodel.PlantViewModel
import com.nulp.edu.auradrip.utils.toTimeAgo
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController? = null) {
    val context = LocalContext.current
    val application = context.applicationContext as AuraDripApplication
    val plantViewModel: PlantViewModel = viewModel(
        factory = PlantViewModel.provideFactory(application.plantRepository)
    )

    val uiState by plantViewModel.uiState.collectAsState()
    val plantConfig by plantViewModel.plantConfig.collectAsState()
    val isRefreshing by plantViewModel.isRefreshing.collectAsState()
    val isWatering by plantViewModel.isWatering.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }

    val commandSentMessage = stringResource(R.string.command_sent)

    LaunchedEffect(Unit) {
        plantViewModel.uiEvent.collectLatest { event ->
            val message = if (event == "command_sent") commandSentMessage else event
            snackbarHostState.showSnackbar(message = message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard), fontWeight = FontWeight.Bold) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
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
                    PlantStatusContent(
                        plant = state.data,
                        config = plantConfig,
                        isWatering = isWatering,
                        onWaterClick = { plantViewModel.forceWaterNow() },
                        onEditConfigClick = { plantId ->
                            navController?.navigate("plant_config/$plantId")
                        }
                    )
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
fun PlantStatusContent(
    plant: PlantStatus,
    config: PlantConfig?,
    isWatering: Boolean,
    onWaterClick: () -> Unit,
    onEditConfigClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val resources = context.resources

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (config != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditConfigClick(plant.plantId) }
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = config.plantName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.plant_settings),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusCard(
                title = stringResource(R.string.moisture),
                value = stringResource(R.string.moisture_value, plant.currentMoisture.toInt()),
                modifier = Modifier.weight(1f)
            )
            StatusCard(
                title = stringResource(R.string.temperature),
                value = stringResource(R.string.temperature_value, plant.currentTemp),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusCard(
                title = stringResource(R.string.air_humidity),
                value = stringResource(R.string.air_humidity_value, plant.currentAirHum),
                modifier = Modifier.weight(1f)
            )
            StatusCard(
                title = stringResource(R.string.age),
                value = resources.getQuantityString(R.plurals.plant_age, plant.ageDays, plant.ageDays),
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onWaterClick,
            enabled = !isWatering,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (isWatering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.water_now),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Text(
            text = stringResource(R.string.updated_time, plant.lastUpdate.toTimeAgo(context)),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun StatusCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
