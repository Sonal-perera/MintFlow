package com.mintflow.personal.adapter

import android.content.Context
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mintflow.personal.R
import com.mintflow.personal.model.Category

class MonthAdapter(context: Context, resource: Int, objects: List<String>) :
    ArrayAdapter<String?>(context, resource, objects) {
    private var monthList: List<String> = objects
    var layout: Int = resource


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.month_dropdown_item, parent, false)
        val textView1 = view.findViewById<TextView>(R.id.textView34)

        val month: String = monthList[position]
        textView1.text = month

        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)

        val textView1 = view.findViewById<TextView>(R.id.textView33)

        val month: String = monthList[position]
        textView1.text = month

        return view
    }
}