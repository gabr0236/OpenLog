package com.example.openlog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.databinding.LayoutLogItemBinding
import com.example.openlog.ui.OnItemClickListenerLogItem
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.util.EmojiRetriever

class LogItemListAdapter(
    private val onItemClickListenerLogItem: OnItemClickListenerLogItem,
    private val selectedCategory: LiveData<LogCategory>
) :
    ListAdapter<LogItem, LogItemListAdapter.ItemViewHolder>(DiffCallback) {
    inner class ItemViewHolder(
        private var binding: LayoutLogItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(logItem: LogItem, onItemClickListenerLogItem: OnItemClickListenerLogItem) {
            binding.apply {
                logItemValue.text = logItem.value.toString()
                logItemDate.text = logItem.date?.let { DateTimeFormatter.formatAsYearDayDateTime(it) }
                editAction.setOnClickListener {
                    onItemClickListenerLogItem.onItemClickedFullLog(logItem)
                }

                val emojiResId = selectedCategory.value?.emojiId?.let {
                    EmojiRetriever.getEmojiIDOf(it)
                }
                imageviewEmoji.setImageDrawable(emojiResId?.let { itemView.context.getDrawable(it) })

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutLogItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onItemClickListenerLogItem)
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