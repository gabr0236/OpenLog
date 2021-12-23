package com.example.openlog.ui

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.LogCategory
import com.example.openlog.LogItemApplication
import com.example.openlog.adapter.LogCategoryListAdapter
import com.example.openlog.data.entity.LogItem
import com.example.openlog.viewmodel.LogItemViewModel
import com.example.openlog.viewmodel.LogItemViewModelFactory
import com.example.openlog.databinding.AddLogItemLayoutBinding
import android.widget.TimePicker

import android.app.TimePickerDialog

import android.widget.DatePicker

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog.OnTimeSetListener
import androidx.databinding.DataBindingUtil
import com.example.openlog.R
import java.util.*


class AddLogItemFragment : Fragment() {
    private val sharedViewModel: LogItemViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        LogItemViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    lateinit var logItem: LogItem

    private val navigationArgs: AddLogItemFragmentArgs by navArgs()

    private var _binding: AddLogItemLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addLogItemLayoutBinding: AddLogItemLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.add_log_item_layout,container, false)
        addLogItemLayoutBinding.addLogItemFragment = this
        _binding=addLogItemLayoutBinding
        return addLogItemLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            this@AddLogItemFragment
        }

        val adapter = LogCategoryListAdapter {
            onCategoryClicked(it)
        }

        binding.recyclerView.adapter = adapter

        sharedViewModel.allLogCategories.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(
            this.context,
            RecyclerView.HORIZONTAL,
            false
        )

        /* TODO: slet?
        sharedViewModel.allLogCategoryNames.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, it)
                binding.logCategorySpinner.adapter = adapter
            }
        }
         */

        val id = navigationArgs.id
        if (id > 0) {
            sharedViewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                logItem = selectedItem
                bind(logItem)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewLogItem()
            }

            sharedViewModel.allLogCategories.value?.first()?.let {
                sharedViewModel.setCategory(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    private fun addNewLogItem() {
        val input = binding.logValue.text.toString()
        if (input.isNullOrBlank()) return //Return if null or blank

        sharedViewModel.addNewLogItem(input)
        binding.logValue.text?.clear()

        val toast = Toast.makeText(requireContext(), "Log Item added", Toast.LENGTH_SHORT)
        toast.show()
    }

private fun updateLogItem() {
    val input = binding.logValue.text.toString()
    if (input.isNullOrBlank()) return //Return if null or blank

        sharedViewModel.updateLogItem(
            this.navigationArgs.id,
            // this.binding.logCategorySpinner.selectedItem.toString(),
            input,
            logItem.date
        )
    binding.logValue.text?.clear()
        val action =
            AddLogItemFragmentDirections.actionAddLogItemFragmentToPreviousLogsFragment()
        findNavController().navigate(action)
}

private fun bind(logItem: LogItem) {
    binding.apply {
        logValue.setText(logItem.value.toString(), TextView.BufferType.SPANNABLE)
        saveAction.setOnClickListener { updateLogItem() }
        // TODO: Set selected category

        // val position = adapter.getPosition(logItem.categoryOwnerName)
        // logCategorySpinner.setSelection(position)
    }
}

@SuppressLint("NotifyDataSetChanged")
private fun onCategoryClicked(logCategory: LogCategory) {
    if (sharedViewModel.setCategory(logCategory)) {
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}
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
                //TODO: doSomethingWith(pickedDateTime)
            }, startHour, startMinute, false).show()
        }, startYear, startMonth, startDay).show()
    }
}