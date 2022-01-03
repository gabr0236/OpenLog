package com.example.openlog.util
import com.example.openlog.R


class EmojiRetriever {
    companion object {
        //TODO: should eventually switch through category emojiID
        fun getEmojiIDOf(emojiId: Int): Int{
            return when(emojiId){
                1 -> emojiArray[0]
                2 -> emojiArray[1]
                3 -> emojiArray[2]
                4 -> emojiArray[3]
                else -> R.drawable.ic_baseline_image_not_supported_24
            }
        }

        val emojiArray: Array<Int> = arrayOf(
            R.drawable.emoji_calories,
            R.drawable.emoji_sugar_blood_level,
            R.drawable.emoji_sleep,
            R.drawable.emoji_insulin
        )
    }
}