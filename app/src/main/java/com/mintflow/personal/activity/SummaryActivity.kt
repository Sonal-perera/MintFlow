package com.mintflow.personal.activity

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.adapter.CustomSpinnerAdapter
import com.mintflow.personal.adapter.ExpenseSummaryAdapter
import com.mintflow.personal.data.CategoryMap
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.databinding.ActivitySummaryBinding
import com.mintflow.personal.model.Summary
import com.mintflow.personal.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class SummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySummaryBinding
    private lateinit var summaryList: MutableList<Summary>
    private var grandTotal: Double = 0.0
    private lateinit var user: User

    private val iconColorMap = CategoryMap().getIconColorMap()
    private val bgColorMap = CategoryMap().getBgColorMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userJson = PrefsHelper.getUser(this)
        user = Gson().fromJson(userJson, User::class.java)

        setupMonthSpinner()
        binding.imageView27.setOnClickListener {
            finish()
        }
        //month Selector
        binding.spinner5.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val month = parent.selectedItemId + 1

                loadExpensesSummary(month.toInt())
                setupPieChart()
                runOnUiThread {
                    if (summaryList.isNotEmpty()) {
                        binding.frameLayout3.visibility = View.GONE
                        binding.pieChart.visibility = View.VISIBLE
                        binding.recyclerView5.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional: Handle case where nothing is selected
            }
        }

        summaryList = mutableListOf();

        val transactionAdapter = ExpenseSummaryAdapter(summaryList, grandTotal, applicationContext)

        val manager1 = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        binding.recyclerView5.layoutManager = manager1
        binding.recyclerView5.adapter = transactionAdapter


    }


    private fun setupMonthSpinner() {
        lifecycleScope.launch {
            val months = withContext(Dispatchers.IO) {
                PrefsHelper.loadMonths(applicationContext)
            }
            val monthAdapter = CustomSpinnerAdapter(
                applicationContext,
                R.layout.rounded_spinner_view,
                R.layout.month_dropdown_item,
                months
            )
            binding.spinner5.adapter = monthAdapter
            binding.spinner5.setSelection(Calendar.getInstance().get(Calendar.MONTH))
        }
    }

    private fun setupPieChart() {
        val pieEntryList = ArrayList<PieEntry>()
        val colorList = ArrayList<Int>()
        val numberColorList = ArrayList<Int>()

        for (summary in summaryList) {
            pieEntryList.add(PieEntry(summary.total.toFloat(), summary.category))

            if (iconColorMap.containsKey(summary.category)) {
                colorList.add(ContextCompat.getColor(this, iconColorMap[summary.category]!!))
            }
            if (bgColorMap.containsKey(summary.category)) {
                numberColorList.add(
                    ContextCompat.getColor(
                        applicationContext,
                        bgColorMap[summary.category]!!
                    )
                )
            }
        }

        val pieDataSet = PieDataSet(pieEntryList, "")
        pieDataSet.setDrawValues(true)
        pieDataSet.valueTextColor = ContextCompat.getColor(this, R.color.light_black)

        pieDataSet.colors = colorList

        val pieData = PieData(pieDataSet).apply {
            setValueTextSize(18f)
            setValueTextColors(numberColorList)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val pieChart = binding.pieChart
            val desc = Description()
            desc.text = "Expense Summary"
            pieChart.data = pieData
            pieChart.description = desc
            pieChart.centerText = "Expense"
            pieChart.setCenterTextTypeface(Typeface.create("bold", R.font.inter_bold))
            pieChart.animateY(1000, Easing.EaseInCirc)
            pieChart.invalidate()
        }, 300)
    }

    private fun loadExpensesSummary(selectedMonth: Int) {
        summaryList.clear()

        // 2) filter & group
        val filtered = PrefsHelper.loadTransactions(this, user.email).filter { tx ->
            tx.type == "Expense" &&
                    Calendar.getInstance().apply { time = tx.date }.let {
                        it.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR) &&
                                it.get(Calendar.MONTH) == (selectedMonth - 1)
                    }
        }
        val totalsByCat = filtered
            .groupBy { it.category }
            .mapValues { it.value.sumOf { tx -> tx.price } }

        // 3) rebuild list
        for ((cat, tot) in totalsByCat) {
            summaryList.add(Summary(cat, tot))
        }

        // 4) recalc grand total
        grandTotal = summaryList.sumOf { it.total }

        // 5) push to adapter
        (binding.recyclerView5.adapter as? ExpenseSummaryAdapter)
            ?.updateData(summaryList, grandTotal)

        // 6) redraw
        binding.recyclerView5.adapter?.notifyDataSetChanged()
    }

}