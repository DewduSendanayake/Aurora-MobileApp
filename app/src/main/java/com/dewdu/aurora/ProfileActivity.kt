package com.dewdu.aurora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val homeButton: ImageButton = findViewById(R.id.navPomodoro)
        homeButton.setOnClickListener {

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val taskButton: ImageButton = findViewById(R.id.navTasks)
        taskButton.setOnClickListener {

            val intent = Intent(this, TaskActivity::class.java)
            startActivity(intent)
        }

        val setButton: ImageButton = findViewById(R.id.navSettings)
        setButton.setOnClickListener {
            // Create an intent to start Onboarding2Activity
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val userButton: ImageButton = findViewById(R.id.navProfile)
        userButton.setOnClickListener {
            // Create an intent to start Onboarding2Activity
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val dltButton: Button = findViewById(R.id.deleteAccountLink)
        dltButton.setOnClickListener {
            // Create an intent to start Onboarding2Activity
            val intent = Intent(this, Onboarding1Activity::class.java)
            startActivity(intent)
        }

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Create an intent to start Onboarding2Activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}