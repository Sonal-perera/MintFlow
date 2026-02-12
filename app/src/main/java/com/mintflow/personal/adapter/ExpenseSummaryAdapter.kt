package com.mintflow.personal.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.activity.TransactionDetailsActivity
import com.mintflow.personal.data.CategoryMap
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.Summary
import com.mintflow.personal.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseSummaryAdapter(
    private var categorySummaries: List<Summary>,
    private var grandTotal: Double,
    private val context: Context
) :
    RecyclerView.Adapter<ExpenseSummaryAdapter.ExpenseSummaryVH>() {

    private val iconColorMap = CategoryMap().getIconColorMap()
    private var total = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseSummaryVH {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.expense_summary_view, parent, false)
        return ExpenseSummaryVH(view)
    }

    @SuppressLint("SetTextI18n", "UseCompatTextViewDrawableApis")
    override fun onBindViewHolder(holder: ExpenseSummaryVH, position: Int) {
        val summary = categorySummaries[position]
        val symbol = PrefsHelper.getSelectedCountrySymbol(context)

        holder.totalAmount.text = "-$symbol${summary.total}"
        holder.category.text = summary.category
        total += summary.total
        val percent =
            ((summary.total / grandTotal) * 100).coerceAtMost(100.0).toInt()

        if (iconColorMap.containsKey(summary.category)) {
            val iconColor = iconColorMap[summary.category]!!
            holder.circle.compoundDrawableTintList =
                ContextCompat.getColorStateList(context, iconColor)
            holder.progress.progressTintList = ContextCompat.getColorStateList(context, iconColor)
        }
        holder.progress.progress = percent
    }

    override fun getItemCount(): Int = categorySummaries.size

    class ExpenseSummaryVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: TextView = itemView.findViewById(R.id.textView69)
        val circle: TextView = itemView.findViewById(R.id.textView70)
        val totalAmount: TextView = itemView.findViewById(R.id.textView71)
        val progress: ProgressBar = itemView.findViewById(R.id.progressBar2)
        val card = itemView
    }

    fun updateData(newItems: List<Summary>, newGrandTotal: Double) {
        this.categorySummaries       = newItems
        this.grandTotal  = newGrandTotal
    }
}