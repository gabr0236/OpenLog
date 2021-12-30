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
            R.layout.layout_log_category -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_log_category, parent, false)
                ItemViewHolder(view)
            }
            R.layout.button_add_category -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.button_add_category, parent, false)
                ButtonViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.button_add_category -> (holder as ButtonViewHolder).bind(
                categoryRecyclerviewHandler
            )
            R.layout.layout_log_category -> (holder as ItemViewHolder).bind(
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
        private val addCategoryCardView: MaterialCardView = view.findViewById(R.id.cardview_create_category)

        fun bind(categoryRecyclerviewHandler: CategoryRecyclerviewHandler) {
            addCategoryCardView.setOnClickListener {
                Log.d("TEST", "Recyclerviewbutton clicket :D")
                categoryRecyclerviewHandler.onCreateCategoryClicked()
            }
        }
    }

    //Returns true if this is the last element
    override fun getItemViewType(position: Int): Int {
        return if (position == logCategories.size) R.layout.button_add_category else R.layout.layout_log_category
    }

    override fun getItemCount(): Int {
        return logCategories.size + 1
    }
}