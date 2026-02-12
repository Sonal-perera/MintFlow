package com.mintflow.personal.fragment

import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.activity.CreateBudgetActivity
import com.mintflow.personal.activity.CreateMonthlyBudgetActivity
import com.mintflow.personal.adapter.BudgetAdapter
import com.mintflow.personal.adapter.MonthAdapter
import com.mintflow.personal.data.NotifyManager
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.User

class BudgetFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var recyclerView: RecyclerView
    private lateinit var user: User
    private lateinit var monthOutOfTotal: TextView
    private lateinit var totalBudgetTextView: TextView
    private lateinit var exceedMsgBtn: MaterialButton
    private lateinit var progressMonth: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)
        val spinner = view.findViewById<Spinner>(R.id.spinner2)
        recyclerView = view.findViewById(R.id.recyclerView2)
        val emptyFrame = view.findViewById<FrameLayout>(R.id.frameLayout2)
        val createBudgetBtn = view.findViewById<MaterialButton>(R.id.createBudgetBtn)
        progressMonth = view.findViewById(R.id.progressBar3)
        monthOutOfTotal = view.findViewById(R.id.textView72)
        totalBudgetTextView = view.findViewById(R.id.textView73)
        exceedMsgBtn = view.findViewById(R.id.button7)
        val setMonthBudgetBtn = view.findViewById<MaterialButton>(R.id.button8)
        context = requireContext()
        val monthList = PrefsHelper.loadMonths(context)
        val userJson = PrefsHelper.getUser(context)
        user = Gson().fromJson(userJson, User::class.java)

        spinner.adapter = MonthAdapter(context, R.layout.month_view_item, monthList)
        val currentMonthIndex = Calendar.getInstance().get(Calendar.MONTH)
        spinner.setSelection(currentMonthIndex)

        setMonthBudgetBtn.setOnClickListener {
            val intent = Intent(requireContext(), CreateMonthlyBudgetActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                loadBudgetData(position)
//                loadMonthlyBudgetData(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle case where nothing is selected
            }
        }

        val budgetList = PrefsHelper.loadCategoryBudgets(context, user.email)
        val budgetAdapter = BudgetAdapter(budgetList, context)

        val manager1 = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = manager1
        recyclerView.adapter = budgetAdapter

        createBudgetBtn.setOnClickListener {
            val intent = Intent(requireContext(), CreateBudgetActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        if (budgetList.isEmpty()) {
            emptyFrame.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyFrame.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        return view
    }

    private fun loadBudgetData(month: Int) {
        val currentCurrency = PrefsHelper.getSelectedCountrySymbol(context)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val transactions = PrefsHelper.loadTransactions(context, user.email)
        val categoryExpenseMap = mutableMapOf<String, Double>()
        var total = 0.0
        var totalBudget = 0.0

        for (transaction in transactions) {
            val calendar = Calendar.getInstance()
            calendar.time = transaction.date
            val transactionYear = calendar.get(Calendar.YEAR)
            val transactionMonth = calendar.get(Calendar.MONTH)

            if (transaction.type == "Expense" && transactionMonth == month && transactionYear == currentYear) {
                val category = transaction.category
                val amount = transaction.price
                total += amount
                categoryExpenseMap[category] =
                    categoryExpenseMap.getOrDefault(category, 0.0) + amount
            }
        }

        val allCategoryBudgets = PrefsHelper.loadCategoryBudgets(context, user.email)
        val allBudgets = PrefsHelper.loadBudgets(context, user.email)
        var budgetTotal = 0.0
        val filteredBudgets = allBudgets
            .filter { it.month == month && it.year == currentYear }
            .map {
                budgetTotal = it.budget
            }

        val filteredCategoryBudgets = allCategoryBudgets
            .filter { it.month == month && it.year == currentYear }
            .map {
                it.spent = categoryExpenseMap[it.category] ?: 0.0
                it
            }
        filteredCategoryBudgets.forEach { budget ->
            totalBudget += budget.budget
        }
        val remaining = budgetTotal - total
        val percent = if (budgetTotal > 0) {
            ((total / budgetTotal) * 100).coerceAtMost(100.0).toInt()
        } else 0



        if (remaining < 0) {
            exceedMsgBtn.visibility = View.VISIBLE
            progressMonth.progressTintList =
                ContextCompat.getColorStateList(context, R.color.red)
        }


        if (remaining.toInt() == 0) {
            exceedMsgBtn.visibility = View.VISIBLE
            exceedMsgBtn.text = "at limit"
            exceedMsgBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.food_icon))
            progressMonth.progressTintList =
                ContextCompat.getColorStateList(context, R.color.gift_icon)
        }
        if (budgetTotal == 0.0) {
            exceedMsgBtn.visibility = View.VISIBLE
            exceedMsgBtn.text = "budget not set"
        }
        monthOutOfTotal.text =
            "$currentCurrency%.2f of $currentCurrency%.2f".format(total, budgetTotal)
        totalBudgetTextView.text = "$currentCurrency $budgetTotal"
        progressMonth.progress = percent

        recyclerView.adapter = BudgetAdapter(filteredCategoryBudgets, context)
    }

}