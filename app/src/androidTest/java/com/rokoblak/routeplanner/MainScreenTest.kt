package com.rokoblak.routeplanner

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.TAG_DRAWER
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.TAG_NAV_BUTTON
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.TAG_SWITCH_DARK_MODE
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

/**
 * Initial instrumentation tests covering some simple user flows, and ensuring the app starts without issue.
 */
@HiltAndroidTest
class MainScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testDrawerOpens() {
        composeTestRule.onNodeWithTag(TAG_NAV_BUTTON).let {
            it.assertIsDisplayed()
            it.performClick()
        }

        composeTestRule.onNodeWithTag(TAG_DRAWER).assertIsDisplayed()
    }

    @Test
    fun testDarkModeIsTogglable() {
        composeTestRule.onNodeWithTag(TAG_NAV_BUTTON).let {
            it.assertIsDisplayed()
            it.performClick()
        }

        composeTestRule.onNodeWithTag(TAG_DRAWER).assertIsDisplayed()

        composeTestRule.onNodeWithTag(TAG_SWITCH_DARK_MODE).let {
            // The data is cleared after each test, but we do initialize the switch to current device dark mode state
            val deviceInNightMode = isDeviceInNightMode()
            if (deviceInNightMode) {
                it.assertIsOn()
            } else {
                it.assertIsOff()
            }
            it.performClick()
            if (deviceInNightMode) {
                it.assertIsOff()
            } else {
                it.assertIsOn()
            }
        }
    }

    private fun isDeviceInNightMode(): Boolean {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val currentMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentMode == Configuration.UI_MODE_NIGHT_YES
    }
}