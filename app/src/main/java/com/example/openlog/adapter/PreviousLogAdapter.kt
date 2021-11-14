package com.example.openlog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.R
import com.google.android.material.button.MaterialButton

class PreviousLogAdapter(private val dataSet: Array<String>) :
        RecyclerView.Adapter<PreviousLogAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val editButton: MaterialButton = itemView.findViewById(R.id.edit_previous_log_button)
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

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            //TODO : midlertidig løsning
            viewHolder.editButton.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_navigation_statistics_to_editLogFragment)
            )
        }
    }