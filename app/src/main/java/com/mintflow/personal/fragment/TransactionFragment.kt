package com.mintflow.personal.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.activity.SummaryActivity
import com.mintflow.personal.adapter.CategoryAdapter
import com.mintflow.personal.adapter.TransactionAdapter
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.Category
import com.mintflow.personal.model.Transaction
import com.mintflow.personal.model.User
import java.util.Calendar

class TransactionFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var monthlyTransactions: MutableList<Transaction>
    private lateinit var recyclerView: RecyclerView
    private lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction, container, false)
        val spinner = view.findViewById<Spinner>(R.id.spinner3)
        recyclerView = view.findViewById(R.id.recyclerView4)
        val summaryBtn = view.findViewById<MaterialButton>(R.id.button2)
        context = requireContext()
        val categoryList = PrefsHelper.loadCategories(context)

        val userJson = PrefsHelper.getUser(context)
        user = Gson().fromJson(userJson, User::class.java)

        spinner.adapter = CategoryAdapter(context, R.layout.category_view_item, categoryList)
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val categoryObj = parent.selectedItem as Category
                val categoryName = categoryObj.name
                loadTransactions(categoryName)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle case where nothing is selected
            }
        }

        monthlyTransactions = mutableListOf()
        val transactionAdapter = TransactionAdapter(monthlyTransactions, context)

        val manager1 = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = manager1
        recyclerView.adapter = transactionAdapter

        summaryBtn.setOnClickListener {
            val intent = Intent(context, SummaryActivity::class.java)
            startActivity(intent)
        }
        return view

    }

    private fun loadTransactions(category: String) {
        val transactions = PrefsHelper.loadTransactions(context, user.email)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        monthlyTransactions.clear()
        for (transaction in transactions) {
            val transactionCalendar = Calendar.getInstance()
            transactionCalendar.time = transaction.date

            val transYear = transactionCalendar.get(Calendar.YEAR)
            val transMonth = transactionCalendar.get(Calendar.MONTH)

            if (transYear == currentYear && transMonth == currentMonth) {
                if (category == "Select Category" || transaction.category == category) {
                    monthlyTransactions.add(transaction)
                }
            }

        }
        recyclerView.adapter?.notifyDataSetChanged()
    }
}