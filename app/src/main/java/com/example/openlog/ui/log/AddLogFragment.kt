package com.example.openlog.ui.log

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.openlog.LogApplication
import com.example.openlog.data.Log
import com.example.openlog.databinding.FragmentAddLogBinding
import com.example.openlog.model.LogViewModel
import com.example.openlog.model.LogViewModelFactory

class AddLogFragment : Fragment() {
    private val viewModel: LogViewModel by activityViewModels {
        LogViewModelFactory(
            (activity?.application as LogApplication).database
                .logDao()
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
        // https://material.io/components/menus/android#exposed-dropdown-menus
        // val adapter = ArrayAdapter(requireContext(), R.layout.fragment_category, categories)
        // (binding.logCategory.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    private fun addNewLogEntry() {
        if (isEntryValid()) {
            viewModel.addNewLogEntry(
                binding.logValue.text.toString()
            )
            // TODO: Reset
            // TODO: Toast
            val action = AddLogFragmentDirections.actionAddLogFragmentToLogListFragment()
            findNavController().navigate(action)
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isLogEntryValid(
            binding.logValue.text.toString()
        )
    }

}