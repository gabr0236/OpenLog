package com.example.openlog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.R
import com.example.openlog.data.entity.LogCategory
import com.google.android.material.card.MaterialCardView


// Setting button as last element: https://stackoverflow.com/questions/29106484/how-to-add-a-button-at-the-end-of-recyclerview/38691600

class LogCategoryListAdapter(private val logCategories: List<LogCategory>,
    private val onLogItemClicked: (LogCategory) -> Unit
) : ListAdapter<LogCategory, LogCategoryListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ItemViewHolder {
        //return if (viewType == CATEGORY) {
        //    ItemViewHolder(LayoutInflater.from(viewGroup.context)
        //        .inflate(R.layout.log_category_layout, viewGroup, false), onLogItemClicked)
        //} else ItemViewHolder(LayoutInflater.from(viewGroup.context)
          //  .inflate(R.layout.add_category_button, viewGroup, false), onLogItemClicked)

        //TODO Slet nedenfor
        return ItemViewHolder(LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.log_category_layout, viewGroup, false), onLogItemClicked)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(logCategories[position], position, itemCount)
    }

    class ItemViewHolder(
        view: View,
        private val onLogItemClicked: (LogCategory) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {
        private val logCategoryName: TextView = view.findViewById(R.id.log_category_name)
        private val logCategoryUnit: TextView = view.findViewById(R.id.log_category_unit)
        private val logCategoryContainer: MaterialCardView = view.findViewById(R.id.log_category_container)
        //TODO private val addLogCategoryButton: Button = view.findViewById(R.id.add_category_button123)
        fun bind(logCategory: LogCategory, position: Int, size: Int) {
            if (position >= size) {
                //TODO addLogCategoryButton.text = "OKI"
            } else {
                logCategoryName.text = logCategory.name
                logCategoryUnit.text = logCategory.unit
                logCategoryContainer.setOnClickListener {
                    onLogItemClicked(logCategory)
                }
                logCategoryContainer.isChecked = logCategory.isSelected
            }
        }
    }

    //Returns true if this is the last element
    override fun getItemViewType(position: Int): Int {
        return if (position == this.itemCount) ADD_CATEGORY_BUTTON else CATEGORY
    }

    override fun getItemCount(): Int {
        return if (HAS_ADD_CATEGORY_BUTTON) {
            currentList.size + 1
        } else currentList.size
    }


    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LogCategory>() {
            override fun areItemsTheSame(
                oldLogCategory: LogCategory,
                newLogCategory: LogCategory
            ): Boolean {
                return oldLogCategory === newLogCategory
            }

            override fun areContentsTheSame(
                oldLogCategory: LogCategory,
                newLogCategory: LogCategory
            ): Boolean {
                return oldLogCategory.name == newLogCategory.name
            }
        }
        private const val HAS_ADD_CATEGORY_BUTTON = true
        private const val ADD_CATEGORY_BUTTON = 1
        private const val CATEGORY = 0
    }
}