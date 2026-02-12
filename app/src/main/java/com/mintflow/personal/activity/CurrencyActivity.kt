package com.mintflow.personal.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mintflow.personal.R
import com.mintflow.personal.adapter.CountryAdapter
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.CountryCurrency

class CurrencyActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val countryList = listOf(
        CountryCurrency("United States", "USD", "$"),
        CountryCurrency("Indonesia", "IDR", "Rp"),
        CountryCurrency("Japan", "JPY", "¥"),
        CountryCurrency("Russia", "RUB", "₽"),
        CountryCurrency("Germany", "EUR", "€"),
        CountryCurrency("Korea", "WON", "₩"),
        CountryCurrency("SriLanka", "LKR", "Rs."),
        CountryCurrency("India", "INR", "₹"),
        CountryCurrency("United Kingdom", "GBP", "£"),
        CountryCurrency("Australia", "AUD", "$"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_currency)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backBtn = findViewById<TextView>(R.id.textView68)


        backBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView3)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val savedCode = PrefsHelper.getSelectedCountry(this)

        val adapter = CountryAdapter(
            countryList, savedCode,
            activity = this
        ) { selected ->
            PrefsHelper.saveSelectedCountry(this, selected.code)
            PrefsHelper.saveSelectedCountrySymbol(this, selected.sign)
            Toast.makeText(this, "Selected: ${selected.name}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = adapter

    }
}