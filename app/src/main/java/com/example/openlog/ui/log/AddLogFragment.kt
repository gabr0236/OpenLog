package com.example.openlog.ui.log

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.switchMap
import androidx.navigation.fragment.findNavController
import com.example.openlog.LogApplication
import com.example.openlog.R
import com.example.openlog.data.entity.Log
import com.example.openlog.databinding.FragmentAddLogBinding
import com.example.openlog.model.LogViewModel
import com.example.openlog.model.LogViewModelFactory

class AddLogFragment : Fragment() {
    private val viewModel: LogViewModel by activityViewModels {
        val db = (activity?.application as LogApplication).database

        LogViewModelFactory(
            db.categoryDao(),
            db.logDao()
        )
    }

    lateinit var log: Log

    private var _binding: FragmentAddLogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveAction.setOnClickListener {
            addNewLogEntry()
        }
        // TODO: Use categories from db
        // https://material.io/components/menus/android#exposed-dropdown-menus
        val adapter = ArrayAdapter(requireContext(), R.layout.fragment_category, listOf("Item 1", "Item 2", "...", "Item N"))
        (binding.logCategory.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    private fun addNewLogEntry() {
        if (isLogEntryValid()) {
            viewModel.addNewLogEntry(
                binding.logValue.text.toString()
            )
            // TODO: Reset
            // TODO: Toast
            val action = AddLogFragmentDirections.actionAddLogFragmentToLogListFragment()
            findNavController().navigate(action)
        }
    }

    private fun isLogEntryValid(): Boolean {
        return viewModel.isLogEntryValid(
            binding.logValue.text.toString()
        )
    }

}