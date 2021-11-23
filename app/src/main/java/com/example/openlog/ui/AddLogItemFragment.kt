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

class AddLogItemFragment : Fragment() {
    private val sharedViewModel: LogItemViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        LogItemViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    lateinit var logItem: LogItem

    private val navigationArgs:  AddLogItemFragmentArgs by navArgs()

    private var _binding: AddLogItemLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddLogItemLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        /*
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
        if (isLogItemValid()) {
            sharedViewModel.addNewLogItem(
                // binding.logCategorySpinner.selectedItem.toString(),
                binding.logValue.text.toString()
            )
            val text = "Log Item added"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(requireContext(), text, duration)
            toast.show()
        }
    }

    private fun updateLogItem() {
        if (isLogItemValid()) {
            sharedViewModel.updateLogItem(
                this.navigationArgs.id,
                // this.binding.logCategorySpinner.selectedItem.toString(),
                this.binding.logValue.text.toString(),
                logItem.date
            )
            val action = AddLogItemFragmentDirections.actionAddLogItemFragmentToLogCategoryListFragment()
            findNavController().navigate(action)
        }
    }

    private fun isLogItemValid(): Boolean {
        return sharedViewModel.isLogItemValid(
            // binding.logCategorySpinner.selectedItem.toString(),
            binding.logValue.text.toString()
        )
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
}