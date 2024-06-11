package com.example.loginsignupsql

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        // Keep the splash screen on for a longer period using a condition
        splashScreen.setKeepOnScreenCondition { true }

        setContent {
            QuickFinanceTheme {
                // Set your splash screen here if you want to customize it
            }
        }

        // Use a coroutine to delay the launch of LoginActivity
        MainScope().launch {
            delaySplashScreen()
        }
    }

    private suspend fun delaySplashScreen() {
        delay(1000)  // Delay for 3 seconds

        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()  // Finish MainActivity to prevent the user from returning to the splash screen
    }
}
