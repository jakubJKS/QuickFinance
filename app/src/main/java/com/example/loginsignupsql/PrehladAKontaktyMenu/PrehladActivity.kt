package com.example.loginsignupsql.PrehladAKontaktyMenu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.loginsignupsql.ui.theme.QuickFinanceTheme

class PrehladActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickFinanceTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PrehladScreen()
                }
            }
        }
    }

    @Composable
    fun PrehladScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { /* No action needed, already on Prehľad */ },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Prehľad")
            }
            Button(
                onClick = {
                    val intent = Intent(this@PrehladActivity, KontaktyActivity::class.java)
                    startActivity(intent)
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Kontakty")
            }
        }
    }
}
