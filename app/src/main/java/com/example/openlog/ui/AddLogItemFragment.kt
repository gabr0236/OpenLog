package com.example.openlog.ui

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.LogItemApplication
import com.example.openlog.adapter.LogCategoryListAdapter
import com.example.openlog.viewmodel.LogItemViewModel
import com.example.openlog.viewmodel.LogItemViewModelFactory
import com.example.openlog.databinding.AddLogFragmentBinding

import android.app.TimePickerDialog

import android.app.DatePickerDialog
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import com.example.openlog.R
import com.example.openlog.util.DateTimeFormatter
import java.util.*


class AddLogItemFragment : Fragment(), CategoryRecyclerviewHandler {
    private val sharedViewModel: LogItemViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        LogItemViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    private var _binding: AddLogFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewCategory: RecyclerView
    private var date: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addLogItemLayoutBinding: AddLogFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.add_log_fragment, container, false)
        addLogItemLayoutBinding.addLogItemFragment = this
        _binding = addLogItemLayoutBinding
        return addLogItemLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Databinding
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            addLogItemFragment = this@AddLogItemFragment
            viewModel = sharedViewModel
        }

        //Log category recyclerview setup
        recyclerViewCategory = binding.recyclerView
        recyclerViewCategory.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        sharedViewModel.allLogCategories.observe(this.viewLifecycleOwner) { items ->
            items.let {
                recyclerViewCategory.adapter = LogCategoryListAdapter(it, this)
            }
        }

        sharedViewModel.allLogCategories.value?.first()?.let {
            sharedViewModel.setCategory(it)
        }

        date = Calendar.getInstance().time //Show current date on screen
        date?.let { binding.textDate.text = DateTimeFormatter.formatDateTime(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    fun addNewLogItem() {
        val input = binding.logValue.text.toString()
        binding.logValue.text?.clear()

        if (input.isBlank() || !input.isDigitsOnly()) return //Return if null or blank

        sharedViewModel.addNewLogItem(input, date)

        val toast = Toast.makeText(requireContext(), "Log Tilføjet", Toast.LENGTH_SHORT)
        toast.show()
        date = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCategoryClicked(logCategory: LogCategory) {
        if (sharedViewModel.setCategory(logCategory)) {
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateCategoryClicked() {
        TODO("Not yet implemented")
    }

    override fun onDeleteCategoryClicked() {
        TODO("Not yet implemented")
    }

    //TODO: lav i anden klasse så denne metode ikke skrives i 2 fragments
    fun pickDateTime() {
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
                date?.let { binding.textDate.text = DateTimeFormatter.formatDateTime(it) }
                Log.d("TEST", "PickDateTime: $pickedDateTime")
            }, startHour, startMinute, true).show()
        }, startYear, startMonth, startDay).show()
    }
}