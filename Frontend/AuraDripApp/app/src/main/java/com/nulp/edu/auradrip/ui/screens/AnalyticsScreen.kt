package com.nulp.edu.auradrip.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
// Vico Core Cartesian Data
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Вибір періоду (7, 14, 30 днів)
            PeriodSelector(
                selectedDays = uiState.selectedDays,
                onDaysSelected = { viewModel.setPeriod(it) }
            )

            // Стан завантаження або відображення графіка
            if (uiState.isLoading && uiState.history == null) {
                Box(Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                uiState.history?.let { history ->
                    VicoThreeChartCard(history, uiState.isCombinedMode)
                } ?: Text(
                    text = "No data available",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Налаштування (Комбінований режим)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = uiState.isCombinedMode,
                    onCheckedChange = { viewModel.toggleChartMode() }
                )
                Text(text = "Combined Chart (Moisture / Temp / Hum)")
            }
        }
    }
}

@Composable
fun VicoThreeChartCard(history: PlantHistory, isCombined: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Побудова моделі даних
            val model = remember(history, isCombined) {
                CartesianChartModel(
                    LineCartesianLayerModel.build {
                        series(history.items.map { it.soilMoisture })

                        if (isCombined) {
                            series(history.items.map { it.airTemperature })
                            series(history.items.map { it.airHumidity })
                        }
                    }
                )
            }

            // 2. Форматування дати
            val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM HH:mm") }

            // Використовуємо x замість value, щоб уникнути конфліктів,
            // та додаємо roundToInt() для точного визначення індексу
            val xValueFormatter = CartesianValueFormatter { _, value, _ ->
                // Тепер 'value' — це Double, і ми можемо спокійно викликати toInt()
                val index = value.toInt()
                history.items.getOrNull(index)?.timestamp
                    ?.atZone(ZoneId.systemDefault())
                    ?.format(dateFormatter) ?: ""
            }

            // 3. Відображення графіка з виправленими осями
            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(),
                    // Використовуємо методи, які відповідають твоїм робочим імпортам
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = xValueFormatter,
                        labelRotationDegrees = 45f
                    ),
                ),
                model = model,
                scrollState = rememberVicoScrollState(),
                modifier = Modifier.height(300.dp)
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