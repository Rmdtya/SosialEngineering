package com.socialengineering.UndanganPernikahan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import pl.droidsonroids.gif.GifImageView
import pl.droidsonroids.gif.GifTexImage2D

class SplashScreen : AppCompatActivity() {

    private val loadingTime: Long = 3000 // 3 seconds
    private val progressBarMaxValue = 100
    private val progressBarUpdateInterval: Long = 100 // Update interval in milliseconds

    private lateinit var progressBarLoading : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        progressBarLoading = findViewById(R.id.progres_bar)
        // Set the maximum value for the ProgressBar
        progressBarLoading.max = progressBarMaxValue

        // Start updating the ProgressBar
        updateProgressBar()

        // Simulate a 3-second delay before navigating to the menu
        Handler().postDelayed({
            // After 3 seconds, navigate to the menu
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // finish the current activity to prevent going back to the loading screen
        }, loadingTime)
    }

    private fun updateProgressBar() {
        val handler = Handler()
        val runnable = object : Runnable {
            var progress = 0
            override fun run() {
                if (progress <= progressBarMaxValue) {
                    // Update the ProgressBar
                    progressBarLoading.progress = progress
                    progress += 4

                    // Run this method again after the specified interval
                    handler.postDelayed(this, progressBarUpdateInterval)
                }
            }
        }

        // Start the initial update
        handler.post(runnable)
    }
}