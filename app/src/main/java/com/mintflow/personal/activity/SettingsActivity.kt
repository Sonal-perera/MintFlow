package com.mintflow.personal.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.mintflow.personal.R
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.listener.ReminderScheduler

class SettingsActivity : AppCompatActivity() {
    private lateinit var currency: TextView
    private lateinit var countryCurrency: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backBtn = findViewById<TextView>(R.id.textView61)
        currency = findViewById(R.id.textView63)
        val currencyBtn = findViewById<ConstraintLayout>(R.id.currency)
        val budgetAlert = findViewById<MaterialSwitch>(R.id.switch2)
        val reminderAlert = findViewById<MaterialSwitch>(R.id.switch3)

        countryCurrency = PrefsHelper.getSelectedCountry(this).toString()
        currency.text = countryCurrency

        budgetAlert.isChecked = PrefsHelper.getNotificationStatus(this)

        budgetAlert.setOnCheckedChangeListener { _, isChecked ->
            PrefsHelper.saveBudgetNotificationStatus(this, isChecked)
        }

        backBtn.setOnClickListener {
            finish()
        }
        currencyBtn.setOnClickListener {
            val intent = Intent(this, CurrencyActivity::class.java)
            startActivity(intent)
        }

        if (PrefsHelper.getReminderStatus(this)) {
            reminderAlert.isChecked = true
        } else {
            reminderAlert.isChecked = false
        }

        reminderAlert.setOnCheckedChangeListener { _, isChecked ->
            PrefsHelper.saveReminderStatus(this, isChecked)
            if (isChecked) {
                checkForPermission()
                ReminderScheduler.scheduleTripleDailyReminders(this)
            } else {
                ReminderScheduler.cancelTripleReminders(this)
            }
        }


    }

    override fun onResume() {
        super.onResume()
        countryCurrency = PrefsHelper.getSelectedCountry(this).toString()
        currency.text = countryCurrency
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                checkForPermission()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkForPermission() {
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }

    }
}