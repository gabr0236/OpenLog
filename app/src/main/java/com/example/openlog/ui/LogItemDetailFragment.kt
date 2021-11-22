package com.example.openlog.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.openlog.LogItemApplication
import com.example.openlog.R
import com.example.openlog.data.entity.LogItem
import com.example.openlog.data.entity.getFormattedDate
import com.example.openlog.databinding.LogItemDetailLayoutBinding
import com.example.openlog.viewmodel.LogItemViewModel
import com.example.openlog.viewmodel.LogItemViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LogItemDetailFragment : Fragment() {
    private val sharedViewModel: LogItemViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        LogItemViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    lateinit var logItem: LogItem

    private val navigationArgs: LogItemDetailFragmentArgs by navArgs()

    private var _binding: LogItemDetailLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LogItemDetailLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.id
        sharedViewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
            logItem = selectedItem
            bind(logItem)
        }
    }

    private fun bind(logItem: LogItem) {
        binding.apply {
            logItemCategory.text = logItem.categoryOwnerName
            logItemValue.text = logItem.value.toString()
            logItemDate.text = logItem.getFormattedDate()
            deleteAction.setOnClickListener { showConfirmationDialog() }
            editAction.setOnClickListener { editLogItem() }
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()
            }
            .show()
    }

    private fun editLogItem() {
        val action = LogItemDetailFragmentDirections.actionLogItemDetailFragmentToAddLogItemFragment(
            getString(R.string.edit_title),
            logItem.id
        )
        this.findNavController().navigate(action)
    }

    private fun deleteItem() {
        sharedViewModel.deleteLogItem(logItem)
        findNavController().navigateUp()
    }
}