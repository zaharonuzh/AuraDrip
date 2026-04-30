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
import androidx.navigation.NavController
import com.nulp.edu.auradrip.LocalLanguage
import com.nulp.edu.auradrip.R
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import com.posthog.PostHog

@Composable
fun SettingsScreen(navController: NavController? = null) {
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
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController?.navigate("plant_config/1") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        ) {
            Text(stringResource(R.string.plant_settings))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.language),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    if (langState.value != "en") {
                        PostHog.capture(
                            event = "language_changed",
                            properties = mapOf(
                                "old_lang" to langState.value,
                                "new_lang" to "en"
                            )
                        )
                        prefs.edit { putString("language", "en") }
                        langState.value = "en"
                    }
                }
        ) {
            RadioButton(
                selected = langState.value == "en",
                onClick = null
            )
            Text(text = stringResource(R.string.english), modifier = Modifier.padding(start = 8.dp))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable {
                    if (langState.value != "uk") {
                        PostHog.capture(
                            event = "language_changed",
                            properties = mapOf(
                                "old_lang" to langState.value,
                                "new_lang" to "uk"
                            )
                        )
                        prefs.edit { putString("language", "uk") }
                        langState.value = "uk"
                    }
                }
        ) {
            RadioButton(
                selected = langState.value == "uk",
                onClick = null
            )
            Text(text = stringResource(R.string.ukrainian), modifier = Modifier.padding(start = 8.dp))
        }
    }
}
