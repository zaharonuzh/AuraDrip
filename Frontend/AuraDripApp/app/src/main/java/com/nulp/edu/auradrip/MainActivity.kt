package com.nulp.edu.auradrip

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.nulp.edu.auradrip.ui.screens.MainScreen
import com.nulp.edu.auradrip.ui.theme.MyApplicationTheme
import java.util.Locale

val LocalLanguage = compositionLocalOf<MutableState<String>> { error("No language provided") }

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val langState = remember { mutableStateOf(prefs.getString("language", "en") ?: "en") }

            val locale = Locale.Builder().setLanguage(langState.value).build()
            val config = LocalConfiguration.current
            config.setLocale(locale)
            val newContext = context.createConfigurationContext(config)

            CompositionLocalProvider(
                LocalContext provides newContext,
                LocalLanguage provides langState
            ) {
                MyApplicationTheme {
                    MainScreen()
                }
            }
        }
    }
}
