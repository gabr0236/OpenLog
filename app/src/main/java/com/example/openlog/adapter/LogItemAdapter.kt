package com.example.openlog.adapter
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
import com.example.openlog.ui.OnItemClickListenerLogItem
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.util.EmojiRetriever


class LogItemAdapter(private val onItemClickListenerLogItem: OnItemClickListenerLogItem,
                          private val selectedCategory: LiveData<LogCategory>,
                          var logItems: List<LogItem>) :
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
        return if (logItems == null) 0 else logItems.size
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return if (logItems[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    //LOG ITEM
    inner class ItemViewHolder(
        var itemView: View
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
        //TODO FIX
        fun bind(){

        }
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
        val item = logItems[position]
        //TODO FIX
        viewHolder.bind()
    }

    private fun populateItemRows(viewHolder: ItemViewHolder, position: Int) {
        val item = logItems[position]
        viewHolder.bind(item,onItemClickListenerLogItem)
    }
}