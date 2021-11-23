package com.example.openlog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.data.entity.getFormattedDate
import com.example.openlog.databinding.LogItemLayoutBinding

class LogItemListAdapter(
    private val onLogItemClicked: (LogItem) -> Unit) :
    ListAdapter<LogItem, LogItemListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogItemListAdapter.ItemViewHolder {
        return ItemViewHolder(
            LogItemLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            ),
            onLogItemClicked
        )
    }

    override fun onBindViewHolder(holder: LogItemListAdapter.ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onLogItemClicked(current)
        }
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: LogItemLayoutBinding,
                         private val onLogItemClicked: (LogItem) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun  bind(logItem: LogItem) {
            binding.apply {
                logItemValue.text = logItem.value.toString()
                logItemDate.text = logItem.getFormattedDate()
                editAction.setOnClickListener {
                    onLogItemClicked(logItem)
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LogItem>() {
            override fun areItemsTheSame(oldLogItem: LogItem, newLogItem: LogItem): Boolean {
                return oldLogItem === newLogItem
            }

            override fun areContentsTheSame(oldLogItem: LogItem, newLogItem: LogItem): Boolean {
                return oldLogItem.date == newLogItem.date
            }
        }
    }
}