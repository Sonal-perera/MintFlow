package com.mintflow.personal.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mintflow.personal.R
import com.mintflow.personal.activity.SettingsActivity
import com.mintflow.personal.model.CountryCurrency

class CountryAdapter(
    private val list: List<CountryCurrency>,
    private var selectedCode: String? = null,
    private val activity: Activity,
    private val onItemClick: (CountryCurrency) -> Unit,
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var selectedPosition: Int = list.indexOfFirst { it.code == selectedCode }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class CountryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameText: TextView = view.findViewById(R.id.textViewCountry)
        private val checkIcon: ImageView = view.findViewById(R.id.imageViewCheck)

        fun bind(position: Int) {
            val item = list[position]
            nameText.text = "${item.name} (${item.code})"
            checkIcon.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                val previous = selectedPosition
                selectedPosition = position
                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)
                onItemClick(item)
                activity.finish()
            }
        }
    }
}
