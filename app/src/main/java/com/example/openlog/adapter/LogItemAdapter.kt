package com.example.openlog.adapter



import androidx.annotation.NonNull

import android.widget.ProgressBar

import androidx.recyclerview.widget.RecyclerView

import android.widget.TextView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.lifecycle.LiveData
import com.example.openlog.R
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.databinding.LayoutLogItemBinding
import com.example.openlog.ui.OnItemClickListenerLogItem
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.util.EmojiRetriever
import com.google.android.material.button.MaterialButton


class RecyclerViewAdapter(private val onItemClickListenerLogItem: OnItemClickListenerLogItem,
                          private val selectedCategory: LiveData<LogCategory>,
                          private val logItems: List<LogItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.layout_log_item, parent, false)
            ItemViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.layout_loading_item, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ItemViewHolder) {
            populateItemRows(viewHolder, position)
        } else if (viewHolder is LoadingViewHolder) {
            showLoadingView(viewHolder, position)
        }
    }

    override fun getItemCount(): Int {
        return if (mItemList == null) 0 else mItemList!!.size
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return if (mItemList!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    //LOG ITEM
    inner class ItemViewHolder(
        private var itemView: View
    ) : RecyclerView.ViewHolder(itemView){
        private val logItemValue: TextView = itemView.findViewById(R.id.log_item_value)
        private val logItemDate: TextView = itemView.findViewById(R.id.log_item_date)
        private val editAction: Button = itemView.findViewById(R.id.edit_action)
        private val imageviewEmoji: ImageView = itemView.findViewById(R.id.imageview_emoji)

        fun bind(logItem: LogItem, onItemClickListenerLogItem: OnItemClickListenerLogItem) {

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

    //LOADING
    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    private fun populateItemRows(viewHolder: ItemViewHolder, position: Int) {
        val item = mItemList!![position]
        viewHolder.tvItem.text = item
    }
}