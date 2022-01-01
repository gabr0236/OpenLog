package com.example.openlog.util
import com.example.openlog.R


class EmojiRetriever {
    companion object {
        //TODO: should eventually switch through category emojiID
        fun getEmojiIDOf(categoryName: String): Int{
            return when(categoryName){
                "Kulhydrater" -> emojiArray[0]
                "Blodsukker" -> emojiArray[1]
                "Søvn" -> emojiArray[2]
                "Insulin" -> emojiArray[3]
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