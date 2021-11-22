package com.example.openlog.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openlog.LogItemApplication
import com.example.openlog.adapter.LogItemListAdapter
import com.example.openlog.databinding.LogCategoryDetailLayoutBinding
import com.example.openlog.util.Statistics
import com.example.openlog.viewmodel.LogItemViewModel
import com.example.openlog.viewmodel.LogItemViewModelFactory

class LogCategoryDetailFragment : Fragment() {
    private val sharedViewModel: LogItemViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        LogItemViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    private val navigationArgs: LogCategoryDetailFragmentArgs by navArgs()

    private var _binding: LogCategoryDetailLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LogCategoryDetailLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LogItemListAdapter {
            val action = LogCategoryDetailFragmentDirections.actionLogCategoryDetailFragmentToLogItemDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        val name = navigationArgs.name
        sharedViewModel.retrieveItemsByCategory(name).observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it.logItems)

                val values = mutableListOf<Int>()
                it.logItems.map {
                    values.add(it.value)
                }
                binding.apply {
                    logCategoryMean.text = Statistics().calculateMean(values).toString()
                    logCategoryStandardDeviation.text = Statistics().calculateStandardDeviation(values).toString()
                }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
    }
}