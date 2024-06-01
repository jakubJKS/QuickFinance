package com.example.loginsignupsql

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()
        Thread.sleep(1500)

        // Po dokončení inicializačnej úlohy spusti LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish()  // Ukončí MainActivity, aby sa užívateľ nevrátil na úvodnú obrazovku po stlačení späť.
    }
}