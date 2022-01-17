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
import com.example.openlog.adapter.LogCategoryAdapter
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.databinding.FragmentEditLogBinding
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.viewmodel.SharedViewModel
import com.example.openlog.viewmodel.SharedViewModelFactory
import java.util.*

class EditLogFragment : DuplicateMethods(), CategoryRecyclerviewHandler {

    private var _binding: FragmentEditLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewCategory: RecyclerView

    private lateinit var logItem: LogItem

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
        binding.buttonEditDate.setOnClickListener{pickDateTime(binding.textDate)}
        //Log category recyclerview setup
        recyclerViewCategory = binding.recyclerView
        recyclerViewCategory.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        sharedViewModel.allLogCategories.observe(this.viewLifecycleOwner) { items ->
            items.let {
                recyclerViewCategory.adapter = LogCategoryAdapter(it, this)
            }
        }

        sharedViewModel.selectedLogItemToEdit.value
            ?: return //return if selectedLogItemToEdit is null
        logItem = sharedViewModel.selectedLogItemToEdit.value!!

        binding.textDate.text =
            logItem.date?.let { DateTimeFormatter.formatAsYearDayDateTime(it) } //Date of log
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
        if (input.isBlank() || !isValidNumber(input)) { //Return if null or blank
            binding.logValue.setText(logItem.value.toString())
            return
        }
        sharedViewModel.updateLogItem(
            logItem.id,
            input,
            this.getDate() ?: logItem.date
        )
        binding.logValue.text?.clear()
        this.setDate(null)

        Toast.makeText(requireContext(), "Log Opdateret", Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.previous_logs_fragment)
    }

    fun deleteLog() {
        //Confirmation dialog
        AlertDialog.Builder(context)
            .setTitle("Slet Log")
            .setMessage("Er du sikker på at du vil slette denne log? Slettet Data kan ikke genskabes.")
            .setIcon(R.drawable.emoji_warning)
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
        if (sharedViewModel.setSelectedCategory(logCategory)) {
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    //TODO: duplicate method
    /**
     * Best suited solution if negative and positive number which can be formatted with '-' and '.'
     */
    private fun isValidNumber(s: String?) : Boolean {
        val regex = """^(-)?[0-9]{0,}((\.){1}[0-9]{1,}){0,1}$""".toRegex()
        return if (s.isNullOrEmpty()) false
        else regex.matches(s)
    }
}