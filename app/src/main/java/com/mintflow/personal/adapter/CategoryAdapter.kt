package com.mintflow.personal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mintflow.personal.R
import com.mintflow.personal.model.Category

class CategoryAdapter(context: Context, resource: Int, objects: List<Category>) :
    ArrayAdapter<Category?>(context, resource, objects) {
    private var categoryList: List<Category> = objects
    var layout: Int = resource


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_dropdown_item, parent, false)
        val textView1 = view.findViewById<TextView>(R.id.textView31)

        val category: Category = categoryList[position]
        textView1.text = category.name

        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)

        val textView1 = view.findViewById<TextView>(R.id.textView30)

        val location: Category = categoryList[position]
        textView1.text = location.name

        return view
    }
}