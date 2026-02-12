package com.mintflow.personal.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mintflow.personal.R
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.CategoryBudget
import androidx.core.graphics.toColorInt
import com.mintflow.personal.data.CategoryMap

class BudgetAdapter(
    private val categoryBudgetCategories: List<CategoryBudget>,
    private val context: Context
) :
    RecyclerView.Adapter<BudgetAdapter.BudgetVH>() {

    private val iconColorMap = CategoryMap().getIconColorMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetVH {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.budget_item_view, parent, false)
        return BudgetVH(view)
    }

    @SuppressLint("SetTextI18n", "UseCompatTextViewDrawableApis")
    override fun onBindViewHolder(holder: BudgetVH, position: Int) {
        val item = categoryBudgetCategories[position]

        val currency = PrefsHelper.getSelectedCountrySymbol(context)
        holder.category.text = item.category
        if (iconColorMap.containsKey(item.category)) {
            holder.categoryCircle.compoundDrawableTintList =
                ContextCompat.getColorStateList(context, iconColorMap[item.category]!!)
            holder.progressBar.progressTintList =
                ContextCompat.getColorStateList(context, iconColorMap[item.category]!!)
        }
        holder.completeOutOf.text =
            currency + item.spent + " of " + currency + item.budget
        val percent =
            ((item.spent / item.budget) * 100).coerceAtMost(100.0).toInt()
        holder.progressBar.progress = percent
        val remaining = item.budget - item.spent

        holder.remainingBudget.text = "Remaining " + currency + remaining

        if (remaining < 0) {
            holder.warningMsg.text = "${item.category} Budget Exceeded!"
            holder.warningMsg.visibility = View.VISIBLE
            holder.warningMsg.setTextColor(Color.RED)
            holder.warningImage.visibility = View.VISIBLE
            holder.progressBar.progressTintList =
                ContextCompat.getColorStateList(context, R.color.red)
            holder.remainingBudget.text = "Exceeded " + currency + "${item.spent - item.budget}"

        }
        if (remaining.toInt() == 0) {
            holder.warningMsg.text = "${item.category} Budget at limit!"
            holder.warningMsg.visibility = View.VISIBLE
            holder.warningMsg.setTextColor(ContextCompat.getColor(context, R.color.gift_icon))
            holder.warningImage.visibility = View.VISIBLE
            holder.progressBar.progressTintList =
                ContextCompat.getColorStateList(context, R.color.gift_icon)
            holder.remainingBudget.text = "Remaining " + currency + "0.0"
            holder.warningImage.setColorFilter("#FFD700".toColorInt())


        }
        if (percent in 81..99) {
            holder.progressBar.progressTintList =
                ContextCompat.getColorStateList(context, R.color.gift_icon)
            holder.warningMsg.text = "Near Limit!"
            holder.warningMsg.visibility = View.VISIBLE
            holder.warningMsg.setTextColor("#FFA500".toColorInt())
            holder.warningImage.visibility = View.VISIBLE
            holder.warningImage.setColorFilter("#FFA500".toColorInt())
        }

    }

    override fun getItemCount(): Int = categoryBudgetCategories.size

    class BudgetVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val warningImage: ImageView = itemView.findViewById(R.id.imageView16)
        val warningMsg: TextView = itemView.findViewById(R.id.textView42)
        val completeOutOf: TextView = itemView.findViewById(R.id.textView41)
        val remainingBudget: TextView = itemView.findViewById(R.id.textView40)
        val category: TextView = itemView.findViewById(R.id.textView67)
        val categoryCircle: TextView = itemView.findViewById(R.id.textView66)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        val card = itemView
    }
}