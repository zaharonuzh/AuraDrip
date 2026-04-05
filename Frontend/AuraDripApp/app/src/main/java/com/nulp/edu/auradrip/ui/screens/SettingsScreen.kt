package com.nulp.edu.auradrip.ui.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nulp.edu.auradrip.LocalLanguage
import com.nulp.edu.auradrip.R

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val langState = LocalLanguage.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = context.getString(R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = context.getString(R.string.language),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    if (langState.value != "en") {
                        prefs.edit().putString("language", "en").apply()
                        langState.value = "en"
                    }
                }
        ) {
            RadioButton(
                selected = langState.value == "en",
                onClick = null
            )
            Text(text = context.getString(R.string.english), modifier = Modifier.padding(start = 8.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    if (langState.value != "uk") {
                        prefs.edit().putString("language", "uk").apply()
                        langState.value = "uk"
                    }
                }
        ) {
            RadioButton(
                selected = langState.value == "uk",
                onClick = null
            )
            Text(text = context.getString(R.string.ukrainian), modifier = Modifier.padding(start = 8.dp))
        }
    }
}
