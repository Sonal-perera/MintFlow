package com.mintflow.personal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mintflow.personal.R

class CustomSpinnerAdapter<T>(
    context: Context,
    private val layoutRes: Int,
    private val dropdownLayoutRes: Int,
    private val items: List<T>
) : ArrayAdapter<T>(context, layoutRes, items) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(dropdownLayoutRes, parent, false)
        val textView = view.findViewById<TextView>(R.id.textView34)
        textView.text = items[position].toString()
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(layoutRes, parent, false)
        val textView = view.findViewById<TextView>(R.id.textView43)
        textView.text = items[position].toString()
        return view
    }
}