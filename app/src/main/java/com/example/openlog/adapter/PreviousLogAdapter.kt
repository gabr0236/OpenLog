package com.example.openlog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.R
import com.google.android.material.card.MaterialCardView

class PreviousLogAdapter(private val dataSet: Array<String>) :
        RecyclerView.Adapter<PreviousLogAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            //TODO: add members
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.previous_log_view, viewGroup, false)
            return ViewHolder(view)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

        override fun onBindViewHolder(viewHolder: PreviousLogAdapter.ViewHolder, position: Int) {
        }

    }