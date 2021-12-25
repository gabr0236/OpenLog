package com.example.openlog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.databinding.LogCategoryLayoutBinding

class LogCategoryListAdapter(
    private val onLogItemClicked: (LogCategory) -> Unit
) :
    ListAdapter<LogCategory, LogCategoryListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        return ItemViewHolder(
            LogCategoryLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            ),
            onLogItemClicked
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class ItemViewHolder(
        private var binding: LogCategoryLayoutBinding,
        private val onLogItemClicked: (LogCategory) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(logCategory: LogCategory) {
            binding.apply {
                logCategoryName.text = logCategory.name
                logCategoryUnit.text = logCategory.unit
                logCategoryContainer.setOnClickListener {
                    onLogItemClicked(logCategory)
                }
                logCategoryContainer.isChecked = logCategory.isSelected
            }
        }
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
    }
}