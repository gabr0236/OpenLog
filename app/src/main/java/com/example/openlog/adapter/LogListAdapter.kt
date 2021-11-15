package com.example.openlog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.constants.Category
import com.example.openlog.data.Log
import com.example.openlog.data.getFormattedDate
import com.example.openlog.databinding.FragmentLogBinding

class LogListAdapter(
    private val onLogClicked: (Log) -> Unit) :
    ListAdapter<Log, LogListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LogListAdapter.ItemViewHolder {
        return ItemViewHolder(
            FragmentLogBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: LogListAdapter.ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onLogClicked(current)
        }
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: FragmentLogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun  bind(log: Log) {
            binding.apply {
                logValue.text = log.value.toString()
                logDate.text = log.getFormattedDate()
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Log>() {
            override fun areItemsTheSame(oldLog: Log, newLog: Log): Boolean {
                return oldLog === newLog
            }

            override fun areContentsTheSame(oldLog: Log, newItem: Log): Boolean {
                return oldLog.date == newItem.date
            }
        }
    }
}