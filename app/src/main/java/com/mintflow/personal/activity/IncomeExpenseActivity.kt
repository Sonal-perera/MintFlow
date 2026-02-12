package com.mintflow.personal.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.adapter.CategoryAdapter
import com.mintflow.personal.data.NotifyManager
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.Category
import com.mintflow.personal.model.Transaction
import com.mintflow.personal.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IncomeExpenseActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var spinner: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_income_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn = findViewById<ImageView>(R.id.imageView11)
        val coverBack = findViewById<ImageView>(R.id.imageView10)
        val title = findViewById<TextView>(R.id.textView25)
        titleEditText = findViewById(R.id.editTextTitle)
        amountEditText = findViewById(R.id.editTextAmount)
        dateEditText = findViewById(R.id.editTextDate)
        val continueBtn = findViewById<MaterialButton>(R.id.continueBtn)
        spinner = findViewById(R.id.spinner)
        val intent = intent
        val type = intent.getStringExtra("type")
        val userJson = PrefsHelper.getUser(this)
        val user = Gson().fromJson(userJson, User::class.java)

        NotifyManager().createNotificationChannel(this)


        backBtn.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }
        val date = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date())
        dateEditText.setText(date)

        val categoryList = PrefsHelper.loadCategories(this)

        spinner.adapter = CategoryAdapter(this, R.layout.category_view_item, categoryList)
        spinner.setSelection(0)


        if (type.equals("Income")) {
            title.text = "Add Income"
            coverBack.setImageResource(R.drawable.design_cover_green)
            continueBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
        } else if (type.equals("Expense")) {
            title.text = "Add Expense"
            coverBack.setImageResource(R.drawable.design_cover_red)
            continueBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        }


        continueBtn.setOnClickListener {
            val titleText = titleEditText.text.toString()
            val amount = amountEditText.text.toString()
            val dateText = dateEditText.text.toString()
            val selectedCategoryObj = spinner.selectedItem as Category
            val selectedCategoryName = selectedCategoryObj.name

            if (titleText.isEmpty()) {
                displayToast("Please enter a title", titleEditText)
            } else if (amount.isEmpty()) {
                displayToast("Please enter an amount", amountEditText)
            } else if (amount == "0" || amount.toDouble() <= 0 || amount == "0.00") {
                displayToast("Please enter a valid amount", amountEditText)
            } else if (selectedCategoryName == "Select Category") {
                displayToast("Please select a category", EditText(this))
            } else if (dateText.isEmpty()) {
                displayToast("Please enter a date", dateEditText)
            } else {
                val newTransaction = parseCustomDate(dateText)?.let { date ->
                    type?.let { type ->
                        Transaction(
                            id = generateTransactionId(),
                            title = titleText,
                            selectedCategoryName,
                            amount.toDouble(),
                            date,
                            type
                        )
                    }
                }

                val transactions = PrefsHelper.loadTransactions(this, user.email)
                if (newTransaction != null) {
                    transactions.add(newTransaction)
                    PrefsHelper.saveTransactions(this, transactions, user.email)
                    resetFields()

                    PrefsHelper.updateAllBudgetSpent(this, user.email)
                    if (PrefsHelper.getNotificationStatus(this)) {
                        NotifyManager().checkAndNotifyCategoryBudgetStatus(
                            this,
                            user,
                            newTransaction
                        )
                        NotifyManager().notifyMonthlyBudgetStatus(
                            this,
                            "Sorry this months' budget just exceeded",
                            user
                        )
                    }

                    Toast.makeText(this, "Transaction Added", Toast.LENGTH_SHORT).show()

                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }
        }

    }

    private fun generateTransactionId(): String {
        val timestamp = System.currentTimeMillis()
        return "TXN_$timestamp"

    }

    private fun parseCustomDate(dateStr: String): Date? {
        val format = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
        return try {
            format.parse(dateStr)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun resetFields() {
        dateEditText.text.clear()
        amountEditText.text.clear()
        titleEditText.text.clear()
        spinner.setSelection(0)
    }

    private fun displayToast(message: String, view: EditText) {
        view.error = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}