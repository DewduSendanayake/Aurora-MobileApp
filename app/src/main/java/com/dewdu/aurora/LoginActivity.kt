package com.dewdu.aurora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val registerButton = findViewById<Button>(R.id.skipButton)

        // Set up a click listener for the register button
        registerButton.setOnClickListener {
            // Create an intent to navigate to SignUpActivity
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Find the button and set up the click listener
        val loginButton: Button = findViewById(R.id.nextButton)
        loginButton.setOnClickListener {
            // Create an intent to start MainUIActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}