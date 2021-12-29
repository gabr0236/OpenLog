package com.example.openlog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.R
import com.example.openlog.data.entity.LogItem
import com.example.openlog.ui.OnItemClickListenerLogItem
import com.example.openlog.util.DateTimeFormatter

//TODO gab: lav til normalt recyclerview uden b.la. diffcallback
class LogItemAdapter(
    private var logItems: List<LogItem>,
    private val onItemClickListenerLogItem: OnItemClickListenerLogItem,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_log_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).bind(logItems[position], onItemClickListenerLogItem)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val logItemValue: TextView = view.findViewById(R.id.log_item_value)
        private val logItemDate: TextView = view.findViewById(R.id.log_item_date)
        private val editAction: Button = view.findViewById(R.id.edit_action)

        fun bind(logItem: LogItem, onItemClickListenerLogItem: OnItemClickListenerLogItem) {
            logItemValue.text = logItem.value.toString()
            logItemDate.text = logItem.date?.let { DateTimeFormatter.formatDateTime(it) }
            editAction.setOnClickListener {
                onItemClickListenerLogItem.onItemClickedFullLog(logItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return logItems.size
    }

    fun replaceListWith(newLogItems: List<LogItem>){
        logItems = newLogItems
        notifyDataSetChanged()
    }
}