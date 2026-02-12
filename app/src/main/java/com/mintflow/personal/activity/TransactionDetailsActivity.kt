package com.mintflow.personal.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.adapter.CategoryAdapter
import com.mintflow.personal.data.NotifyManager
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.data.PrefsHelper.loadTransactions
import com.mintflow.personal.listener.AmountListener
import com.mintflow.personal.model.Category
import com.mintflow.personal.model.Transaction
import com.mintflow.personal.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intent = intent
        val transactionJson = intent.getStringExtra("transaction")
        val transaction = Gson().fromJson(transactionJson, Transaction::class.java)
        val backBtn = findViewById<TextView>(R.id.textView52)
        val dateTime = findViewById<TextView>(R.id.textView53)
        val currency = findViewById<TextView>(R.id.textView54)
        val deleteBtn = findViewById<TextView>(R.id.textView55)
        val type = findViewById<TextView>(R.id.textView58)
        val category = findViewById<TextView>(R.id.textView59)
        val amount = findViewById<EditText>(R.id.editTextBudget2)
        val title = findViewById<EditText>(R.id.editTextBudget3)
        val spinner = findViewById<Spinner>(R.id.spinner4)
        val editBtn = findViewById<MaterialButton>(R.id.editTransactionBtn)
        val background = findViewById<ConstraintLayout>(R.id.main2)
        val userJson = PrefsHelper.getUser(this)
        val user = Gson().fromJson(userJson, User::class.java)

        NotifyManager().createNotificationChannel(this)

        AmountListener().divide(amount)
        dateTime.text =
            SimpleDateFormat("EEEE d MMMM yyyy hh:mm a", Locale.ENGLISH).format(transaction.date)
        currency.text = PrefsHelper.getSelectedCountrySymbol(this)
        title.setText(transaction.title)
        type.text = transaction.type
        category.text = transaction.category
        amount.setText(transaction.price.toString())

        if (transaction.type == "Income") {
            background.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            editBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.green))

        } else if (transaction.type == "Expense") {
            background.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
            editBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        }

        val categoryList = PrefsHelper.loadCategories(this)

        spinner.adapter = CategoryAdapter(this, R.layout.category_view_item, categoryList)



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position)
                val selectedCategoryObj = selectedItem as Category
                val selectedCategoryName = selectedCategoryObj.name
                if (selectedCategoryName != "Select Category") {
                    category.text = selectedCategoryName
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        backBtn.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }

        editBtn.setOnClickListener @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS) {
            val amountText = amount.text.toString()
            val titleText = title.text.toString()
            val cleanedAmount = amountText.replace(",", "")

            if (titleText.isEmpty()) {
                displayToast("Please enter a title", title)
            } else if (amountText.isEmpty()) {
                displayToast("Please enter an amount", amount)
            } else if (cleanedAmount == "0" || cleanedAmount.toDouble() <= 0 || cleanedAmount == "0.00") {
                displayToast("Please enter a valid amount", amount)
            } else if (category.text == "Select Category") {
                displayToast("Please select a category", EditText(this))
            } else {
                val editTransaction = Transaction(
                    id = transaction.id,
                    title = titleText,
                    category.text.toString(),
                    cleanedAmount.toDouble(),
                    Date(),
                    transaction.type
                )

                val transactions = loadTransactions(this, user.email)
                transactions.add(editTransaction)

                PrefsHelper.updateTransaction(this, editTransaction, user.email)
                PrefsHelper.updateAllBudgetSpent(this, user.email)

                if (PrefsHelper.getNotificationStatus(this)) {
                    NotifyManager().checkAndNotifyCategoryBudgetStatus(
                        this,
                        user,
                        editTransaction
                    )
                    NotifyManager().notifyMonthlyBudgetStatus(
                        this,
                        "Sorry this months' budget just exceeded",
                        user
                    )
                }
                Toast.makeText(this, "Transaction Updated", Toast.LENGTH_SHORT).show()
                navigateToHome()

            }
        }

        deleteBtn.setOnClickListener {
            val transactionList = loadTransactions(this, user.email).toMutableList()
            transactionList.remove(transaction)
            PrefsHelper.saveTransactions(this, transactionList, user.email)
            Toast.makeText(this, "Transaction Deleted", Toast.LENGTH_SHORT).show()
            navigateToHome()
        }


    }

    private fun displayToast(message: String, view: EditText) {
        view.error = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}