package com.example.openlog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.R
import com.example.openlog.models.LogCategory

class CategoryAdapter(private val logCategories: Array<LogCategory>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val logTypeText: TextView = view.findViewById(R.id.text_category)
        private val logUnitText: TextView = view.findViewById(R.id.text_unit)

        fun bind(logCategory: LogCategory){
            logTypeText.text=logCategory.getTypeString()
            logUnitText.text=logCategory.getUnitString()
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.category_view, viewGroup, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(logCategories[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = logCategories.size

}