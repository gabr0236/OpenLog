package com.example.openlog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.openlog.R
import com.example.openlog.util.EmojiRetriever


class EmojiArrayAdapter(private val mContext: Context, private val emojiList: Array<Int?>) :
    ArrayAdapter<Int>(mContext, 0, emojiList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItem = convertView

        if (listItem == null) listItem =
            LayoutInflater.from(mContext).inflate(R.layout.list_item_emoji, parent, false)

        val currentEmojiId = emojiList[position]
        val emojiImage: ImageView? = listItem?.findViewById(R.id.imageview_emoji)
        currentEmojiId?.let { EmojiRetriever.getEmojiIDOf(it) }
            ?.let { emojiImage?.setImageResource(it) }

        return listItem!! //TODO: fjern "!!"
    }
}