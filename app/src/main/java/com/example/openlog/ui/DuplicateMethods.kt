package com.example.openlog.ui

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
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.util.showCustomToast
import com.example.openlog.viewmodel.SharedViewModel
import com.example.openlog.viewmodel.SharedViewModelFactory
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

    fun setDate(newDate: Date?) {
        date = newDate
    }

    fun getDate(): Date? {
        return date
    }

    fun onCreateCategoryClicked() {
        findNavController().navigate(R.id.create_category_fragment)
    }

    fun onDeleteCategoryClicked(logCategory: LogCategory) {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.delete_category))
            .setMessage(getString(R.string.delete_question))
            .setIcon(R.drawable.emoji_warning)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                //If yes is selected
                //Ask for confirmation
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.delete_category))
                    .setMessage(getString(R.string.delete_question_2))
                    .setIcon(R.drawable.emoji_warning)
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        //If yes is selected
                        Toast(context).showCustomToast(
                            getString(R.string.category_deleted),
                            R.drawable.emoji_checkmark,
                            true,
                            requireActivity()
                        )
                        sharedViewModel.deleteCategory(logCategory)
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .show()
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