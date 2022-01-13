package com.example.openlog.util
import com.example.openlog.R


class EmojiRetriever {
    companion object {
        fun getEmojiIDOf(emojiId: Int): Int{
            return when(emojiId){
                1 -> emojiArray[0]
                2 -> emojiArray[1]
                3 -> emojiArray[2]
                4 -> emojiArray[3]
                5 -> emojiArray[4]
                6 -> emojiArray[5]
                7 -> emojiArray[6]
                8 -> emojiArray[7]
                9 -> emojiArray[8]
                10 -> emojiArray[9]
                11 -> emojiArray[10]
                12 -> emojiArray[11]
                13 -> emojiArray[12]
                14 -> emojiArray[13]
                15 -> emojiArray[14]
                16 -> emojiArray[15]
                17 -> emojiArray[16]
                18 -> emojiArray[17]
                19 -> emojiArray[18]
                20 -> emojiArray[19]
                21 -> emojiArray[20]
                22 -> emojiArray[21]
                23 -> emojiArray[22]
                24 -> emojiArray[23]
                25 -> emojiArray[24]
                26 -> emojiArray[25]
                27 -> emojiArray[26]
                28 -> emojiArray[27]
                else -> R.drawable.ic_baseline_image_not_supported_24
            }
        }

        val emojiArray: Array<Int> = arrayOf(
            R.drawable.emoji_calories,
            R.drawable.emoji_sugar_blood_level,
            R.drawable.emoji_insulin,
            R.drawable.emoji_sleep,
            R.drawable.emoji_arm,
            R.drawable.emoji_books,
            R.drawable.emoji_burger,
            R.drawable.emoji_calculator,
            R.drawable.emoji_cashless_payment,
            R.drawable.emoji_creative_thinking,
            R.drawable.emoji_dart_board,
            R.drawable.emoji_diagram,
            R.drawable.emoji_graphic_design,
            R.drawable.emoji_hammer,
            R.drawable.emoji_moon,
            R.drawable.emoji_mop,
            R.drawable.emoji_mortarboard,
            R.drawable.emoji_music_player,
            R.drawable.emoji_namaste,
            R.drawable.emoji_notebook,
            R.drawable.emoji_open_book,
            R.drawable.emoji_painting,
            R.drawable.emoji_seeding,
            R.drawable.emoji_sword,
            R.drawable.emoji_uterus,
            R.drawable.emoji_videogame,
            R.drawable.emoji_yoga_pose2,
            R.drawable.emoji_yoga_pose3,
        )

        fun getEmojiIdIndexes(): Array<Int?> {
            val arr = arrayOfNulls<Int>(emojiArray.size)
            for(i in 1..arr.size) arr[i-1]=i
            return arr
        }
    }
}