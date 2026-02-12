package com.mintflow.personal.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.adapter.TransactionAdapter
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.Transaction
import com.mintflow.personal.model.User
import java.util.Calendar
import java.util.Date

class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val context = view.context;
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val name = view.findViewById<TextView>(R.id.textView6)
        val welcomeText = view.findViewById<TextView>(R.id.textView8)
        val balanceText = view.findViewById<TextView>(R.id.textView10)
        val incomeText = view.findViewById<TextView>(R.id.textView11)
        val expenseText = view.findViewById<TextView>(R.id.textView13)

        val userJson = PrefsHelper.getUser(context)
        val currency = PrefsHelper.getSelectedCountrySymbol(context)
        val user = Gson().fromJson(userJson, User::class.java)

        val currentCalendar = Calendar.getInstance()
        val hour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val period = getTimePeriod(hour)

        val currentYear = currentCalendar.get(Calendar.YEAR)
        val currentMonth = currentCalendar.get(Calendar.MONTH)

        val transactions = PrefsHelper.loadTransactions(context, user.email)
        val monthlyTransactions = mutableListOf<Transaction>()
        var totalIncome = 0.0
        var totalExpense = 0.0

        for (transaction in transactions) {
            val transactionCalendar = Calendar.getInstance()
            transactionCalendar.time = transaction.date

            val transYear = transactionCalendar.get(Calendar.YEAR)
            val transMonth = transactionCalendar.get(Calendar.MONTH)

            if (transYear == currentYear && transMonth == currentMonth) {
                monthlyTransactions.add(transaction)
                if (transaction.type == "Income") {
                    totalIncome += transaction.price
                } else if (transaction.type == "Expense") {
                    totalExpense += transaction.price
                }
            }
        }

        name.text = user.fullName
        welcomeText.text = "Good $period,"
        balanceText.text = currency + " " + (totalIncome - totalExpense).toString()
        incomeText.text = currency + " " + totalIncome.toString()
        expenseText.text = currency + " " + totalExpense.toString()

        if ((totalIncome - totalExpense) >= 0.0) {
            balanceText.setTextColor(resources.getColor(R.color.white))
        } else {
            balanceText.setTextColor(resources.getColor(R.color.dark_red))
        }

        val transactionAdapter = TransactionAdapter(monthlyTransactions, context)

        val manager1 = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = manager1
        recyclerView.adapter = transactionAdapter

        return view
    }

    private fun getTimePeriod(hour: Int): String {
        return when (hour) {
            in 5..11 -> "Morning"
            in 12..16 -> "Afternoon"
            in 17..20 -> "Evening"
            else -> "Night"
        }
    }
}