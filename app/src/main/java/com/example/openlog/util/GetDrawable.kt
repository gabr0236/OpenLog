package com.example.openlog.util
import android.content.Context
import com.example.openlog.R
import java.lang.IllegalArgumentException


class GetDrawable {
    companion object {
        fun getEmojiIDOf(categoryName: String): Int{
            return when(categoryName){
                "Kulhydrater" -> R.drawable.emoji_calories
                "Blodsukker" -> R.drawable.emoji_sugar_blood_level
                "Insulin" -> R.drawable.emoji_vaccine
                "Søvn" -> R.drawable.emoji_sleep
                else -> R.drawable.ic_baseline_image_not_supported_24
            }
        }
    }
}