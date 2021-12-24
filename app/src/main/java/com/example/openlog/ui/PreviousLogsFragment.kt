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
import com.example.openlog.R
import com.example.openlog.adapter.LogCategoryListAdapter
import com.example.openlog.adapter.LogItemListAdapter
import com.example.openlog.data.entity.LogItem
import com.example.openlog.databinding.PreviousLogLayoutBinding
import com.example.openlog.viewmodel.LogItemViewModel
import com.example.openlog.viewmodel.LogItemViewModelFactory

class PreviousLogsFragment : Fragment(), OnItemClickListenerLogItem {
    private val sharedViewModel: LogItemViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        LogItemViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    private var _binding: PreviousLogLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var lineGraph: LineGraph
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var recyclerViewLogItem: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PreviousLogLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.allLogCategories.value?.first()?.let {
            sharedViewModel.setCategory(it)
        }

        //Log category recyclerview setup
        recyclerViewCategory = binding.logCategoryRecyclerView
        recyclerViewCategory.layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        recyclerViewCategory.adapter = LogCategoryListAdapter { onCategoryClicked(it) }
        sharedViewModel.allLogCategories.observe(this.viewLifecycleOwner) { items ->
            items.let {
                (recyclerViewCategory.adapter as LogCategoryListAdapter).submitList(it)
            }
        }

        //Log item recyclerview setup
        recyclerViewLogItem = binding.logItemRecyclerView
        recyclerViewLogItem.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        recyclerViewLogItem.adapter = LogItemListAdapter(this)
        sharedViewModel.retrieveItemsByCategory(sharedViewModel.selectedCategory.value?.name.toString()).observe(this.viewLifecycleOwner) { items ->
            items.logItems.let {
                (recyclerViewLogItem.adapter as LogItemListAdapter).submitList(it)
            }
        }
        recyclerViewLogItem.scrollToPosition(0)

        //Graph
        lineGraph = LineGraph(sharedViewModel.logValues(), binding.logGraph)
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
            lineGraph.setValues(sharedViewModel.logValues())
        }
    }

    override fun onItemClickedFullLog(logItem: LogItem) {
        sharedViewModel.setSelectedLogItemToEdit(logItem)
        findNavController().navigate(R.id.edit_log_fragment)
    }
}