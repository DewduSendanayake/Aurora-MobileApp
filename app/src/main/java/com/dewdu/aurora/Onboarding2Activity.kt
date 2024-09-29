package com.dewdu.aurora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Onboarding2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding2)

        val nextButton: Button = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {

            val intent = Intent(this, Onboarding3Activity::class.java)
            startActivity(intent)
        }


        val skipButton: Button = findViewById(R.id.skipButton)
        skipButton.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

//        val backButton: Button = findViewById(R.id.backButton)
//        skipButton.setOnClickListener {
//
//            val intent = Intent(this, Onboarding1Activity::class.java)
//            startActivity(intent)
//        }


    }
}