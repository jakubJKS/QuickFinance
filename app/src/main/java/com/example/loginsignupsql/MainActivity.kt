package com.example.loginsignupsql

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            QuickFinanceTheme {
                // Set your splash screen here if you want to customize it
            }
        }

        // Po dokončení inicializačnej úlohy spusti LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish()  // Ukončí MainActivity, aby sa užívateľ nevrátil na úvodnú obrazovku po stlačení späť.
    }
}
