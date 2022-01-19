package com.example.openlog.util

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.openlog.R

class InputValidator {
    companion object {
        /**
         * Best suited solution if negative and positive number which can be formatted with '-' and '.'
         */
        fun isValidNumber(context: Context, activity: Activity, s: String?): Boolean {
            val regex = """^(-)?[0-9]{0,}((\.){1}[0-9]{1,}){0,1}$""".toRegex()
            return if (s.isNullOrEmpty() || s.isBlank()) {
                Toast(context).showCustomToast(
                    context.getString(R.string.msg_no_input),
                    R.drawable.emoji_x,
                    true,
                    activity
                )
                false
            } else if (s.contains(",")) {
                Toast(context).showCustomToast(
                    context.getString(R.string.dot_not_comma),
                    R.drawable.emoji_x,
                    false,
                    activity
                )
                false
            } else if (!regex.matches(s)) {
                Toast(context).showCustomToast(
                    context.getString(R.string.msg_invalid_input),
                    R.drawable.emoji_x,
                    true,
                    activity
                )
                false
            } else true
        }
    }
}