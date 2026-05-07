package com.nulp.edu.auradrip.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nulp.edu.auradrip.R
import com.nulp.edu.auradrip.domain.model.PlantHistory
import com.nulp.edu.auradrip.ui.viewmodel.AnalyticsViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.analytics), fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp) // Відступи між картками
        ) {
            // Вибір періоду (7, 14, 30 днів)
            PeriodSelector(
                selectedDays = uiState.selectedDays,
                onDaysSelected = { viewModel.setPeriod(it) }
            )

            // Стан завантаження або відображення трьох графіків
            if (uiState.isLoading && uiState.history == null) {
                Box(Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                uiState.history?.let { history ->
                    // 1. Графік вологості ґрунту
                    AnalyticsChartCard(
                        title = "Soil Moisture (%)",
                        history = history,
                        data = history.items.map { it.soilMoisture }
                    )

                    // 2. Графік температури повітря
                    AnalyticsChartCard(
                        title = "Air Temperature (°C)",
                        history = history,
                        data = history.items.map { it.airTemperature }
                    )

                    // 3. Графік вологості повітря
                    AnalyticsChartCard(
                        title = "Air Humidity (%)",
                        history = history,
                        data = history.items.map { it.airHumidity }
                    )
                } ?: Text(
                    text = "No data available",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun AnalyticsChartCard(
    title: String,
    history: PlantHistory,
    data: List<Float>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Побудова моделі даних для конкретної серії
            val model = remember(data) {
                CartesianChartModel(
                    LineCartesianLayerModel.build { series(data) }
                )
            }

            val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM HH:mm") }
            val xValueFormatter = CartesianValueFormatter { _, value, _ ->
                val index = value.toInt()
                history.items.getOrNull(index)?.timestamp
                    ?.atZone(ZoneId.systemDefault())
                    ?.format(dateFormatter) ?: ""
            }

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = xValueFormatter,
                        labelRotationDegrees = 45f
                    ),
                ),
                model = model,
                scrollState = rememberVicoScrollState(),
                modifier = Modifier.height(220.dp) // Трохи менша висота для кожної картки
            )
        }
    }
}

@Composable
fun PeriodSelector(selectedDays: Int, onDaysSelected: (Int) -> Unit) {
    val periods = listOf(7, 14, 30)
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        periods.forEachIndexed { index, days ->
            SegmentedButton(
                selected = selectedDays == days,
                onClick = { onDaysSelected(days) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = periods.size)
            ) {
                Text("${days}d")
            }
        }
    }
}