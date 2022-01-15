package com.example.openlog.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.R
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.ui.OnItemClickListenerLogItem
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.util.EmojiRetriever

class LogItemPagingAdapter(private val onItemClickListenerLogItem: OnItemClickListenerLogItem,
                           private val selectedCategory: LiveData<LogCategory>
): PagingDataAdapter<LogItem, LogItemPagingAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    inner class ItemViewHolder(itemView: View
    ) : RecyclerView.ViewHolder(itemView){
        private val logItemValue: TextView = itemView.findViewById(R.id.log_item_value)
        private val logItemDate: TextView = itemView.findViewById(R.id.log_item_date)
        private val editAction: Button = itemView.findViewById(R.id.edit_action)
        private val imageviewEmoji: ImageView = itemView.findViewById(R.id.imageview_emoji)

        fun bind(logItem: LogItem?, onItemClickListenerLogItem: OnItemClickListenerLogItem) {
            logItemValue.text = logItem?.value.toString()
            logItemDate.text = logItem?.date?.let { DateTimeFormatter.formatAsYearDayDateTime(it) }
            editAction.setOnClickListener {
                onItemClickListenerLogItem.onItemClickedFullLog(logItem)
            }

            val emojiResId = selectedCategory.value?.emojiId?.let {
                EmojiRetriever.getEmojiIDOf(it)
            }
            imageviewEmoji.setImageDrawable(emojiResId?.let { itemView.context.getDrawable(it) })
        }
    }

    override fun onBindViewHolder(holder: LogItemPagingAdapter.ItemViewHolder, position: Int) {
        val current: LogItem? = getItem(position)
        Log.d("TEST", "BIND ITEM AT POSITION: $position, is null?: $current}")
        holder.bind(current, onItemClickListenerLogItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogItemPagingAdapter.ItemViewHolder {
        Log.d("TEST", "CREATE VIEWHOLDER PAGING ADAPTER")
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_log_item, parent, false))
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LogItem>(){
            override fun areItemsTheSame(oldItem: LogItem, newItem: LogItem) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: LogItem, newItem: LogItem) = oldItem == newItem
        }
    }
}