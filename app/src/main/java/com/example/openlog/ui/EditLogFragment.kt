package com.example.openlog.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.LogItemApplication
import com.example.openlog.R
import com.example.openlog.adapter.LogCategoryListAdapter
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.databinding.FragmentEditLogBinding
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.viewmodel.LogItemViewModel
import com.example.openlog.viewmodel.LogItemViewModelFactory
import java.util.*

class EditLogFragment : Fragment(), CategoryRecyclerviewHandler {
    private val sharedViewModel: LogItemViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        LogItemViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    private var _binding: FragmentEditLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewCategory: RecyclerView

    private lateinit var logItem: LogItem
    private var date: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editLogLayoutBinding: FragmentEditLogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_log, container, false)
        editLogLayoutBinding.editLogFragment = this
        _binding = editLogLayoutBinding
        return editLogLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Databinding
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            editLogFragment = this@EditLogFragment
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

        sharedViewModel.selectedLogItemToEdit.value
            ?: return //return if selectedLogItemToEdit is null
        logItem = sharedViewModel.selectedLogItemToEdit.value!!

        binding.textDate.text =
            logItem.date?.let { DateTimeFormatter.formatDateTime(it) } //Date of log
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    fun updateLogItem() {
        val input = binding.logValue.text.toString()
        if (input.isBlank() || !input.isDigitsOnly()) { //Return if null or blank
            binding.logValue.setText(logItem.value.toString())
            return
        }
        sharedViewModel.updateLogItem(
            logItem.id,
            input,
            date ?: logItem.date
        )
        binding.logValue.text?.clear()
        date = null

        Toast.makeText(requireContext(), "Log Opdateret", Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.previous_logs_fragment)
    }

    fun deleteLog() {
        //Confirmation dialog
        AlertDialog.Builder(context)
            .setTitle("Slet Log")
            .setMessage("Er du sikker på at du vil slette denne log? Slettet Data kan ikke genskabes.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                //If yes is selected
                Toast.makeText(
                    context,
                    "Log Slettet",
                    Toast.LENGTH_SHORT
                ).show()
                sharedViewModel.deleteLogItem(logItem)
                findNavController().navigate(R.id.previous_logs_fragment)
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCategoryClicked(logCategory: LogCategory) {
        if (sharedViewModel.setCategory(logCategory)) {
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateCategoryClicked() {
        findNavController().navigate(R.id.create_category_fragment)
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
                Log.d("TEST", "PickDateTime: ${pickedDateTime}")
            }, startHour, startMinute, true).show()
        }, startYear, startMonth, startDay).show()
    }
}