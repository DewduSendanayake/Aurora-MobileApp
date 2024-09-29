package com.dewdu.aurora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        spinner = findViewById(R.id.spinner)

        // Load saved timer setting (if exists)
        val sharedPref = getSharedPreferences("AuroraPrefs", Context.MODE_PRIVATE)
        val savedTime = sharedPref.getInt("selected_time", 25) // Default to 25 minutes
        spinner.setSelection(getSpinnerIndex(savedTime))

        // Set a listener on the spinner to save the selected time
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: android.view.View, position: Int, id: Long
            ) {
                val selectedTimeText = parent.getItemAtPosition(position).toString()
                val selectedTime = selectedTimeText.substringBefore(" ").toInt() // Extract minutes

                // Save selected time using SharedPreferences
                val editor = sharedPref.edit()
                editor.putInt("selected_time", selectedTime)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No-op
            }
        }

        // Navigation buttons
        setupNavigationButtons()
    }

    private fun getSpinnerIndex(minutes: Int): Int {
        return when (minutes) {
            25 -> 0
            30 -> 1
            45 -> 2
            60 -> 3
            else -> 0 // Default to 25 minutes if not found
        }
    }

    private fun setupNavigationButtons() {
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
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val userButton: ImageButton = findViewById(R.id.navProfile)
        userButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
