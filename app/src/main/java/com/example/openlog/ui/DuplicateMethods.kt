package com.example.openlog.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.openlog.LogItemApplication
import com.example.openlog.R
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.databinding.FragmentAddLogBinding
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.viewmodel.SharedViewModel
import com.example.openlog.viewmodel.SharedViewModelFactory
import org.w3c.dom.Text
import java.util.*

abstract class DuplicateMethods : Fragment() {
    val sharedViewModel: SharedViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        SharedViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    private var date: Date? = null

    fun setDate(newDate: Date?){
        date = newDate
    }

    fun getDate(): Date? {
        return date
    }

    fun onCreateCategoryClicked(){
        findNavController().navigate(R.id.create_category_fragment)
    }

    fun onDeleteCategoryClicked(logCategory: LogCategory) {
        //Confirmation dialog
        AlertDialog.Builder(context)
            .setTitle(R.string.delete_category)
            .setMessage(R.string.delete_category_confirmation)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                //If yes is selected
                Toast.makeText(
                    context,
                    R.string.category_deleted,
                    Toast.LENGTH_SHORT
                ).show()
                sharedViewModel.deleteCategory(logCategory)
                findNavController().navigate(R.id.previous_logs_fragment)
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    fun pickDateTime(binding: TextView) {
        Log.d("TEST", "PickDateTime clicked")
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                date = pickedDateTime.time
                date?.let {
                    if (binding != null) {
                        binding.text = DateTimeFormatter.formatAsYearDayDateTime(it)
                    }
                }
                Log.d("TEST", "PickDateTime: $pickedDateTime")
            }, startHour, startMinute, true).show()
        }, startYear, startMonth, startDay).show()
    }
}