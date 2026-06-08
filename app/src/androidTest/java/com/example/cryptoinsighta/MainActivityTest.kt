package com.example.cryptoinsighta

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cryptoinsighta.ui.screen.CreaMainScreen
import com.example.cryptoinsighta.ui.theme.CryptoInsightaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMainScreenCaricamentoElementiGrafici() {
        composeTestRule.setContent {
            CryptoInsightaTheme {
                CreaMainScreen()
            }
        }

        composeTestRule.onNodeWithText("CryptoInsighta").assertIsDisplayed()

        composeTestRule.onNodeWithText("Valore del portafoglio totale").assertIsDisplayed()


        composeTestRule.onNodeWithText("Performance").assertIsDisplayed()

        composeTestRule.onNodeWithText("assets").assertIsDisplayed()
    }
}