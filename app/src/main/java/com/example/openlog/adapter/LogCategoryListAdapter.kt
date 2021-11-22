package com.example.openlog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.data.entity.getFormattedDate
import com.example.openlog.databinding.LogCategoryLayoutBinding
import com.example.openlog.databinding.LogItemLayoutBinding

class LogCategoryListAdapter(
    private val onLogItemClicked: (LogCategory) -> Unit) :
    ListAdapter<LogCategory, LogCategoryListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogCategoryListAdapter.ItemViewHolder {
        return ItemViewHolder(
            LogCategoryLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: LogCategoryListAdapter.ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onLogItemClicked(current)
        }
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: LogCategoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun  bind(logCategory: LogCategory) {
            binding.apply {
                logCategoryName.text = logCategory.name
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LogCategory>() {
            override fun areItemsTheSame(oldLogCategory: LogCategory, newLogCategory: LogCategory): Boolean {
                return oldLogCategory === newLogCategory
            }

            override fun areContentsTheSame(oldLogCategory: LogCategory, newLogCategory: LogCategory): Boolean {
                return oldLogCategory.name == newLogCategory.name
            }
        }
    }
}