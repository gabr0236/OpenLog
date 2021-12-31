package com.example.openlog.util
import com.example.openlog.R


class EmojiViewRetriever {
    companion object {
        //TODO: should eventually switch through category emoji ID
        fun getEmojiIDOf(categoryName: String): Int{
            return when(categoryName){
                "Kulhydrater" -> R.drawable.emoji_calories
                "Blodsukker" -> R.drawable.emoji_sugar_blood_level
                "Insulin" -> R.drawable.emoji_insulin
                "Søvn" -> R.drawable.emoji_sleep
                else -> R.drawable.ic_baseline_image_not_supported_24
            }
        }
    }
}