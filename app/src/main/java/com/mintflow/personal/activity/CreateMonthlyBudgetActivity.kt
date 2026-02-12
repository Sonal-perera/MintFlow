package com.mintflow.personal.activity

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.data.NotifyManager
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.listener.AmountListener
import com.mintflow.personal.model.Budget
import com.mintflow.personal.model.Category
import com.mintflow.personal.model.CategoryBudget
import com.mintflow.personal.model.User

class CreateMonthlyBudgetActivity : AppCompatActivity() {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_monthly_budget)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backBtn = findViewById<TextView>(R.id.textView75)
        val currency = findViewById<TextView>(R.id.textView77)
        val budgetEditText = findViewById<EditText>(R.id.editTextBudget4)
        val addBudgetBtn = findViewById<MaterialButton>(R.id.addMonthBudgetBtn)
        val userJson = PrefsHelper.getUser(this)
        user = Gson().fromJson(userJson, User::class.java)

        currency.text = PrefsHelper.getSelectedCountrySymbol(this)
        AmountListener().divide(budgetEditText)

        backBtn.setOnClickListener {
            navigateToHome()
        }

        addBudgetBtn.setOnClickListener {
            val amount = budgetEditText.text.toString()
            val clearText = amount.replace(",", "")

            if (amount.isEmpty()) {
                budgetEditText.error = "Enter amount"
                Toast.makeText(this, "Select category", Toast.LENGTH_SHORT).show()
            } else if (clearText == "0" || clearText == "0.00") {
                budgetEditText.error = "Enter valid amount"
                Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
            } else {

                val newBudget = Budget(
                    spent = 0.0,
                    budget = clearText.toDouble(),
                    month = Calendar.getInstance().get(Calendar.MONTH),
                    year = Calendar.getInstance().get(Calendar.YEAR),
                )

                val budgets = PrefsHelper.loadBudgets(this, user.email)
                budgets.add(newBudget)

                PrefsHelper.saveBudget(this, budgets, user.email)
                PrefsHelper.updateAllBudgetSpent(
                    this, user.email
                )
                if (PrefsHelper.getNotificationStatus(this)) {
//                    NotifyManager().checkAndNotifyCategoryBudgetStatus(this, user)
                }


                Toast.makeText(this, "Budget set", Toast.LENGTH_SHORT).show()
                navigateToHome()

            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("from", "createBudget")
        startActivity(intent)
        finish()
    }
}