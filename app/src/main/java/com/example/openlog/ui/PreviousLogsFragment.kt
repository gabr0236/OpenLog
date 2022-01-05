package com.example.openlog.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asFlow
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.LogItemApplication
import com.example.openlog.R
import com.example.openlog.adapter.LogCategoryAdapter
import com.example.openlog.adapter.LogItemListAdapter
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.databinding.FragmentPreviousLogsBinding
import com.example.openlog.viewmodel.SharedViewModel
import com.example.openlog.viewmodel.SharedViewModelFactory
import kotlin.math.log

class PreviousLogsFragment : Fragment(), OnItemClickListenerLogItem, CategoryRecyclerviewHandler {
    private val sharedViewModel: SharedViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        SharedViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    private var _binding: FragmentPreviousLogsBinding? = null
    private val binding get() = _binding!!
    private lateinit var lineGraph: LineGraph
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var recyclerViewLogItem: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val previousLogsLayoutBinding: FragmentPreviousLogsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_previous_logs, container, false)
        previousLogsLayoutBinding.previousLogFragment = this
        _binding = previousLogsLayoutBinding
        return previousLogsLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            previousLogFragment = this@PreviousLogsFragment
            viewModel = sharedViewModel
        }


        //Log category recyclerview setup
        recyclerViewCategory = binding.logCategoryRecyclerView
        recyclerViewCategory.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        sharedViewModel.allLogCategories.observe(this.viewLifecycleOwner) { items ->
            items.let {
                recyclerViewCategory.adapter = LogCategoryAdapter(it, this)
            }
            sharedViewModel.setSelectedCategory(items.first())
        }

        //Log item recyclerview setup
        recyclerViewLogItem = binding.logItemRecyclerView
        recyclerViewLogItem.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        val logItemListAdapter = LogItemListAdapter(this, sharedViewModel.selectedCategory)
        logItemListAdapter.submitList(sharedViewModel.logsOfSelectedCategory())
        recyclerViewLogItem.adapter=logItemListAdapter

        sharedViewModel.selectedCategory.observe(this.viewLifecycleOwner) {
            val logsOfSelectedCategory = sharedViewModel.logsOfSelectedCategory()
            (recyclerViewLogItem.adapter as LogItemListAdapter).submitList(logsOfSelectedCategory)
            lineGraph = LineGraph(sharedViewModel.logValuesAndDates(), binding.logGraph)
            Log.d("TEST", "allLogItems size: ${sharedViewModel.allLogItems.value?.size}")

        }
        recyclerViewLogItem.scrollToPosition(0)

        sharedViewModel.allLogItems.observe(this.viewLifecycleOwner) { } //TODO ?? nothing here
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCategoryClicked(logCategory: LogCategory) {
        if (sharedViewModel.setSelectedCategory(logCategory)) { }
    }

    override fun onCreateCategoryClicked() {
        findNavController().navigate(R.id.create_category_fragment)
    }

    override fun onDeleteCategoryClicked(logCategory: LogCategory) {
        TODO("Not yet implemented")
    }

    override fun onItemClickedFullLog(logItem: LogItem) {
        sharedViewModel.setSelectedLogItemToEdit(logItem)
        findNavController().navigate(R.id.edit_log_fragment)
    }
}