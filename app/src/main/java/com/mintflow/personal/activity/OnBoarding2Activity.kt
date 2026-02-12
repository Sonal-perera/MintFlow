package com.mintflow.personal.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mintflow.personal.R
import com.mintflow.personal.adapter.OnBoardingSliderAdapter
import com.mintflow.personal.model.SliderItem
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView


class OnBoarding2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_on_boarding2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val signUpBtn = findViewById<Button>(R.id.button5)
        val loginBtn = findViewById<Button>(R.id.button6)

        val sliderView = findViewById<SliderView>(R.id.imageSlider)
        val sliderItems = mutableListOf<SliderItem>()

        sliderItems.add(
            SliderItem(
                "Gain total control of your money",
                "Become your own money manager and make every cent count",
                R.drawable.ilustration_1
            )
        )
        sliderItems.add(
            SliderItem(
                "Know where your money goes",
                "Track your transaction easily, with categories and financial report ",
                R.drawable.illustration_2
            )
        )
        sliderItems.add(
            SliderItem(
                "Planning ahead",
                "Setup your budget for each category so you in control",
                R.drawable.illustration_3
            )
        )

        val adapter = OnBoardingSliderAdapter(this)
        adapter.renewItems(sliderItems)

        sliderView.setSliderAdapter(adapter)

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        sliderView.indicatorSelectedColor = getColor(R.color.primary)
        sliderView.indicatorRadius = 5
        sliderView.indicatorUnselectedColor = Color.WHITE
        sliderView.scrollTimeInSec = 2
        sliderView.startAutoCycle()

        signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
        loginBtn.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }

    }
}