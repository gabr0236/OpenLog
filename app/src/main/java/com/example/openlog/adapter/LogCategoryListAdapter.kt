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
import com.google.android.material.card.MaterialCardView
import java.lang.IllegalArgumentException


// Setting button as last element: https://stackoverflow.com/questions/29106484/how-to-add-a-button-at-the-end-of-recyclerview/38691600
// , https://newbedev.com/how-to-add-a-button-at-the-end-of-recyclerview

class LogCategoryListAdapter(private val logCategories: List<LogCategory>,
    private val onLogItemClicked: (LogCategory) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.log_category_layout -> {
                val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.log_category_layout, parent, false)
                ItemViewHolder(view) { onLogItemClicked }
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
        when(getItemViewType(position)){
            R.layout.add_category_button -> (holder as ButtonViewHolder).bind()
            R.layout.log_category_layout -> (holder as ItemViewHolder).bind(logCategories[position])
        }
    }

    inner class ItemViewHolder(view: View, private val onLogItemClicked: (LogCategory) -> Unit) : RecyclerView.ViewHolder(view) {
        private val logCategoryName: TextView = view.findViewById(R.id.log_category_name)
        private val logCategoryUnit: TextView = view.findViewById(R.id.log_category_unit)
        private val logCategoryContainer: MaterialCardView = view.findViewById(R.id.log_category_container)

        fun bind(logCategory: LogCategory) {
                logCategoryName.text = logCategory.name
                logCategoryUnit.text = logCategory.unit
                logCategoryContainer.setOnClickListener {
                    onLogItemClicked(logCategory)
                }
                logCategoryContainer.isChecked = logCategory.isSelected
        }
    }

    inner class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val addLogCategoryButton: Button = view.findViewById(R.id.create_category_button)

        fun bind(){
            Log.d("TEST", "Correct path")
            addLogCategoryButton.setOnClickListener {
                Log.d("TEST", "Recyclerviewbutton clicket :D")
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