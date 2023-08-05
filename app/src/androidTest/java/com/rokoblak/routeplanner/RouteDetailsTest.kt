package com.rokoblak.routeplanner

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.rokoblak.routeplanner.ui.feature.routedetails.composables.TAG_ROUTE_HEADER
import com.rokoblak.routeplanner.ui.feature.routelisting.composables.TAG_ROUTE
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

/**
 * UI tests covering entering the route details screen
 */
@HiltAndroidTest
class RouteDetailsTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testRouteDetailsOpens() {
        composeTestRule.onAllNodesWithTag(TAG_ROUTE)[0].let {
            it.assertIsDisplayed()
            it.performClick()
        }

        composeTestRule.onNodeWithTag(TAG_ROUTE_HEADER).let {
            it.assertIsDisplayed()
        }
    }
}