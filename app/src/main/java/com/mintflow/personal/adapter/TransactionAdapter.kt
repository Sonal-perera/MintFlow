package com.mintflow.personal.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mintflow.personal.R
import com.mintflow.personal.activity.TransactionDetailsActivity
import com.mintflow.personal.data.CategoryMap
import com.mintflow.personal.data.PrefsHelper
import com.mintflow.personal.model.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private val transactions: List<Transaction>,
    private val context: Context
) :
    RecyclerView.Adapter<TransactionAdapter.TransactionVH>() {

    private val categoryMap = CategoryMap()
    private val iconMap = categoryMap.getIconMap()
    private val iconColorMap = categoryMap.getIconColorMap()
    private val bgColorMap = categoryMap.getBgColorMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.transaction_view_item, parent, false)
        return TransactionVH(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransactionVH, position: Int) {
        val transaction = transactions[position]

        val symbol = PrefsHelper.getSelectedCountrySymbol(context)
        val date = transaction.date
        val price = symbol + transaction.price.toString()
        val formatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH) // or "HH:mm" for 24hr format
        val stringDate = formatter.format(date)

        holder.title.text = transaction.title
        holder.category.text = transaction.category
        holder.time.text = stringDate


        if (transaction.type == "Expense") {
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.red))
            holder.price.text = "- $price"
        } else if (transaction.type == "Income") {
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.green))
            holder.price.text = "+ $price"
        }

        if (iconMap.containsKey(transaction.category)) {
            holder.image.setColorFilter(
                ContextCompat.getColor(
                    context,
                    iconColorMap[transaction.category]!!
                )
            )
            holder.image.backgroundTintList =
                ContextCompat.getColorStateList(context, bgColorMap[transaction.category]!!)
            holder.image.setImageResource(iconMap[transaction.category]!!)
        }

        holder.card.setOnClickListener {
            val intent = Intent(context, TransactionDetailsActivity::class.java)
            val gson = Gson()
            val transactionJson = gson.toJson(transaction)
            intent.putExtra("transaction", transactionJson)
            ContextCompat.startActivity(context, intent, null)
            if (context is Activity) {
                context.finish()
            }

        }

    }

    override fun getItemCount(): Int = transactions.size

    class TransactionVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageView7)
        val category: TextView = itemView.findViewById(R.id.textView21)
        val title: TextView = itemView.findViewById(R.id.textView22)
        val price: TextView = itemView.findViewById(R.id.textView23)
        val time: TextView = itemView.findViewById(R.id.textView24)
        val card = itemView
    }
}