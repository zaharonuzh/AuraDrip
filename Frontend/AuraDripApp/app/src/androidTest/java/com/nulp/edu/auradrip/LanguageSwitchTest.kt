package com.nulp.edu.auradrip

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LanguageSwitchTest {

    @Before
    fun setUp() {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = targetContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("language", "en").commit()
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testLanguageSwitchUpdatesUI() {
        composeTestRule.waitForIdle()

        // Go to settings tab
        composeTestRule.onAllNodesWithText("Settings", ignoreCase = true, useUnmergedTree = true).onFirst().performClick()
        composeTestRule.waitForIdle()

        // Click Ukrainian Row
        composeTestRule.onNodeWithText("Ukrainian", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        // Verify the Settings tab title changes to Ukrainian
        composeTestRule.onAllNodesWithText("Налаштування", useUnmergedTree = true).onFirst().assertExists()
        
        // Go to Dashboard tab via Bottom Navigation
        composeTestRule.onAllNodesWithText("Головна", useUnmergedTree = true).onFirst().performClick()
        composeTestRule.waitForIdle()

        // Verify Dashboard screen title is in Ukrainian
        composeTestRule.onAllNodesWithText("Головна", useUnmergedTree = true).onFirst().assertExists()
    }
}
