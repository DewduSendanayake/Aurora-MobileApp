package com.dewdu.aurora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var timerCircle: TextView
    private lateinit var startFocus: Button
    private lateinit var takeBreak: Button
    private lateinit var resetButton: Button

    private var focusTimeInMillis: Long = 25 * 60 * 1000 // Default 25 minutes
    private var breakTimeInMillis: Long = 5 * 60 * 1000 // Default 5 minutes
    private var timer: CountDownTimer? = null
    private var isTimerRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Initialize views
        timerCircle = findViewById(R.id.timerCircle)
        startFocus = findViewById(R.id.startFocus)
        takeBreak = findViewById(R.id.takeBreak)
        resetButton = findViewById(R.id.reset)

        // Retrieve selected time from SharedPreferences
        val sharedPref = getSharedPreferences("AuroraPrefs", Context.MODE_PRIVATE)
        val selectedTime = sharedPref.getInt("selected_time", 25) // Default to 25 minutes
        focusTimeInMillis = (selectedTime * 60 * 1000).toLong() // Convert to milliseconds

        // Update the initial timer text
        updateTimerText(focusTimeInMillis)

        // Set click listener for the start button
        startFocus.setOnClickListener {
            if (!isTimerRunning) {
                startFocusTimer()
            }
        }

        // Set click listener for the break button
        takeBreak.setOnClickListener {
            if (isTimerRunning) {
                stopTimer()
            } else {
                startBreakTimer()
            }
        }

        // Set click listener for the reset button
        resetButton.setOnClickListener {
            resetTimer()
        }

        // Setup navigation buttons
        setupNavigationButtons()
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

    private fun startFocusTimer() {
        timer = object : CountDownTimer(focusTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                focusTimeInMillis = millisUntilFinished
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
                isTimerRunning = false
                updateTimerText(0)
                // Notify user when timer finishes
            }
        }.start()

        isTimerRunning = true
    }

    private fun startBreakTimer() {
        timer = object : CountDownTimer(breakTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                breakTimeInMillis = millisUntilFinished
                updateTimerText(millisUntilFinished)
            }

            override fun onFinish() {
                isTimerRunning = false
                updateTimerText(0)
                // Notify user when break finishes
            }
        }.start()

        isTimerRunning = true
    }

    private fun stopTimer() {
        timer?.cancel()
        isTimerRunning = false
    }

    private fun resetTimer() {
        stopTimer()
        // Reset to saved focus time
        focusTimeInMillis = getSharedPreferences("AuroraPrefs", Context.MODE_PRIVATE)
            .getInt("selected_time", 25) * 60 * 1000L
        updateTimerText(focusTimeInMillis)
    }

    private fun updateTimerText(timeInMillis: Long) {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60
        timerCircle.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
