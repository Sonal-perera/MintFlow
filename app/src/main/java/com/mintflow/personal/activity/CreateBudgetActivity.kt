package com.mintflow.personal.activity

import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.adapter.CategoryAdapter
import com.mintflow.personal.data.NotifyManager
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.listener.AmountListener
import com.mintflow.personal.model.Category
import com.mintflow.personal.model.CategoryBudget
import com.mintflow.personal.model.User

class CreateBudgetActivity : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var currency: String

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_budget)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn = findViewById<TextView>(R.id.textView36)
        val currencyTextView = findViewById<TextView>(R.id.textView39)
        val budgetEditText = findViewById<EditText>(R.id.editTextBudget)
        val spinner = findViewById<Spinner>(R.id.spinner4)
        val addBudgetBtn = findViewById<MaterialButton>(R.id.addBudgetBtn)
        val userJson = PrefsHelper.getUser(this)
        user = Gson().fromJson(userJson, User::class.java)

        NotifyManager().createNotificationChannel(this)

        currency = PrefsHelper.getSelectedCountrySymbol(this).toString()
        currencyTextView.text = currency

        val categoryList = PrefsHelper.loadCategories(this)

        spinner.adapter = CategoryAdapter(this, R.layout.category_view_item, categoryList)
        spinner.setSelection(0)

        backBtn.setOnClickListener {
            navigateToHome()
        }


        addBudgetBtn.setOnClickListener {
            val amount = budgetEditText.text.toString()
            val categoryObj = spinner.selectedItem as Category
            val selectedCategory = categoryObj.name
            val clearText = amount.replace(",", "")

            if (amount.isEmpty()) {
                budgetEditText.error = "Enter amount"
                Toast.makeText(this, "Select category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (clearText == "0" || clearText == "0.00") {
                budgetEditText.error = "Enter valid amount"
                Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (selectedCategory == "Select Category") {
                Toast.makeText(this, "Select category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {

                // Load monthly and category budgets
                val allBudgets = PrefsHelper.loadBudgets(this, user.email)
                val allCategoryBudgets = PrefsHelper.loadCategoryBudgets(this, user.email)
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH)

                val monthlyBudget = allBudgets
                    .firstOrNull { it.month == currentMonth && it.year == currentYear }
                    ?.budget ?: 0.0

                val existingCategoryBudgetTotal = allCategoryBudgets
                    .filter { it.month == currentMonth && it.year == currentYear }
                    .sumOf { it.budget }
                val existingCategory = allCategoryBudgets
                    .filter { it.month == currentMonth && it.year == currentYear && it.category == selectedCategory }

                val newCategoryBudgetAmount = clearText.toDouble()

                // NOW checking if (existing total + new one) exceeds monthly
                if ((existingCategoryBudgetTotal + newCategoryBudgetAmount) > monthlyBudget) {
                    AlertDialog.Builder(this)
                        .setTitle("Reminder")
                        .setCancelable(true)
                        .setPositiveButton("Okay", null)
                        .setIcon(AppCompatResources.getDrawable(this, R.drawable.warning))
                        .setMessage("Adding this category budget will exceed your monthly budget limit of $currency $monthlyBudget.")
                        .show()
                    return@setOnClickListener
                }

                if (existingCategory.isNotEmpty()) {
                    AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setCancelable(true)
                        .setPositiveButton("Okay", null)
                        .setIcon(AppCompatResources.getDrawable(this, R.drawable.logo))
                        .setMessage("Category Already added")
                        .show()
                    return@setOnClickListener
                }

                // Else add the new category budget
                val newCategoryBudget = CategoryBudget(
                    category = selectedCategory,
                    spent = 0.0,
                    budget = newCategoryBudgetAmount,
                    month = currentMonth,
                    year = currentYear,
                )

                val budgets = PrefsHelper.loadCategoryBudgets(this, user.email)
                budgets.add(newCategoryBudget)

                PrefsHelper.saveCategoryBudgets(this, budgets, user.email)
                PrefsHelper.updateAllBudgetSpent(this, user.email)

                Toast.makeText(this, "Category Budget added", Toast.LENGTH_SHORT).show()
                navigateToHome()

            }
        }

        AmountListener().divide(budgetEditText)
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("from", "createBudget")
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        PrefsHelper.updateAllBudgetSpent(this, user.email)
    }
}