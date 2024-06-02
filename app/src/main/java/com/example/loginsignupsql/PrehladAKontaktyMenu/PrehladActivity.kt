package com.example.loginsignupsql.com.example.loginsignupsql.PrehladAKontaktyMenu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loginsignupsql.databinding.ActivityPrehladBinding

class PrehladActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrehladBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrehladBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPrehlad.setOnClickListener {
            // Already on Prehľad, no action needed
        }

        binding.buttonKontakty.setOnClickListener {
            val intent = Intent(this, KontaktyActivity::class.java)
            startActivity(intent)
        }
    }
}
