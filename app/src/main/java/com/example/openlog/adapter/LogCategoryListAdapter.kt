package com.example.openlog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.R
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.ui.CategoryRecyclerviewHandler
import com.google.android.material.card.MaterialCardView


// Setting button as last element: https://newbedev.com/how-to-add-a-button-at-the-end-of-recyclerview

class LogCategoryListAdapter(
    private val logCategories: List<LogCategory>,
    private val categoryRecyclerviewHandler: CategoryRecyclerviewHandler
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.log_category_layout -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.log_category_layout, parent, false)
                ItemViewHolder(view)
            }
            R.layout.add_category_button -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.add_category_button, parent, false)
                ButtonViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.add_category_button -> (holder as ButtonViewHolder).bind(
                categoryRecyclerviewHandler
            )
            R.layout.log_category_layout -> (holder as ItemViewHolder).bind(
                logCategories[position],
                categoryRecyclerviewHandler
            )
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val logCategoryName: TextView = view.findViewById(R.id.log_category_name)
        private val logCategoryUnit: TextView = view.findViewById(R.id.log_category_unit)
        private val logCategoryContainer: MaterialCardView =
            view.findViewById(R.id.log_category_container)

        fun bind(
            logCategory: LogCategory,
            categoryRecyclerviewHandler: CategoryRecyclerviewHandler
        ) {
            logCategoryName.text = logCategory.name
            logCategoryUnit.text = logCategory.unit
            logCategoryContainer.setOnClickListener {
                categoryRecyclerviewHandler.onCategoryClicked(logCategory)
            }
            logCategoryContainer.isChecked = logCategory.isSelected
        }
    }

    inner class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val addLogCategoryButton: Button = view.findViewById(R.id.create_category_button)

        fun bind(categoryRecyclerviewHandler: CategoryRecyclerviewHandler) {
            addLogCategoryButton.setOnClickListener {
                Log.d("TEST", "Recyclerviewbutton clicket :D")
                categoryRecyclerviewHandler.onCreateCategoryClicked()
            }
        }
    }

    //Returns true if this is the last element
    override fun getItemViewType(position: Int): Int {
        return if (position == logCategories.size) R.layout.add_category_button else R.layout.log_category_layout
    }

    override fun getItemCount(): Int {
        return logCategories.size + 1
    }
}