package com.example.openlog.util

import android.app.Activity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.openlog.R

fun Toast.showCustomToast(
    message: String,
    imageResourceId: Int,
    durationShort: Boolean,
    activity: Activity
) {
    val layout = activity.layoutInflater.inflate(
        R.layout.toast_custom,
        activity.findViewById(R.id.cardview_toast)
    )

    //Set text
    val textView: TextView = layout.findViewById(R.id.textview_toast)
    textView.text = message

    //Set img
    val imageView: ImageView = layout.findViewById(R.id.imageView_toast)
    imageView.setImageResource(imageResourceId)

    // use the application extension function
    this.apply {
        duration = if (durationShort) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        view = layout
        show()
    }
}