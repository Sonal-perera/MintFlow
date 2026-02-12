package com.mintflow.personal.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.mintflow.personal.R

class OnBoardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_on_boarding)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val getStartedBtn = findViewById<MaterialButton>(R.id.getStartedBtn)
        val navigateToLoginBtn = findViewById<TextView>(R.id.navigateToLoginFromGetStartedBtn)

        getStartedBtn.setOnClickListener {
            startActivity(Intent(this, OnBoarding2Activity::class.java))
            finish()
        }

        navigateToLoginBtn.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }

    }
}