package com.example.openlog.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.LogCategory
import com.example.openlog.LogItemApplication
import com.example.openlog.adapter.LogCategoryListAdapter
import com.example.openlog.adapter.LogItemListAdapter
import com.example.openlog.databinding.LogCategoryListLayoutBinding
import com.example.openlog.viewmodel.LogItemViewModel
import com.example.openlog.viewmodel.LogItemViewModelFactory

class LogCategoryListFragment : Fragment() {
    private val sharedViewModel: LogItemViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        LogItemViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    private var _binding: LogCategoryListLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LogCategoryListLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.allLogCategories.value?.first()?.let {
            sharedViewModel.setCategory(it)
        }

        val logCategoryAdapter = LogCategoryListAdapter {
            onCategoryClicked(it)
        }
        binding.logCategoryRecyclerView.adapter = logCategoryAdapter

        sharedViewModel.allLogCategories.observe(this.viewLifecycleOwner) { items ->
            items.let {
                logCategoryAdapter.submitList(it)
            }
        }

        binding.logCategoryRecyclerView.layoutManager = LinearLayoutManager(
            this.context,
            RecyclerView.HORIZONTAL,
            false
        )

        val logItemAdapter = LogItemListAdapter {
            val action = LogCategoryListFragmentDirections.actionLogCategoryListFragmentToAddLogItemFragment("Edit", it.id)
            findNavController().navigate(action)
        }
        binding.logItemRecyclerView.adapter = logItemAdapter

        sharedViewModel.retrieveItemsByCategory(sharedViewModel.selectedCategory.value?.name.toString()).observe(this.viewLifecycleOwner) { items ->
            items.logItems.let {
                logItemAdapter.submitList(it)
            }
        }

        sharedViewModel.allLogItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                logItemAdapter.submitList(it)
            }
        }

        binding.logItemRecyclerView.layoutManager = LinearLayoutManager(this.context)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onCategoryClicked(logCategory: LogCategory) {
        if (sharedViewModel.setCategory(logCategory)) {
            binding.logCategoryRecyclerView.adapter?.notifyDataSetChanged()

            binding.logItemRecyclerView.adapter?.notifyDataSetChanged()

            sharedViewModel.retrieveItemsByCategory(sharedViewModel.selectedCategory.value?.name.toString()).observe(this.viewLifecycleOwner) { items ->
                items.logItems.let {
                    (binding.logItemRecyclerView.adapter as LogItemListAdapter).submitList(it)
                }
            }
        }
    }
}