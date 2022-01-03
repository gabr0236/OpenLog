package com.example.openlog.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        sharedViewModel.allLogCategories.value?.first()?.let {
            sharedViewModel.setCategory(it)
        }

        //Log category recyclerview setup
        recyclerViewCategory = binding.logCategoryRecyclerView
        recyclerViewCategory.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        sharedViewModel.allLogCategories.observe(this.viewLifecycleOwner) { items ->
            items.let {
                recyclerViewCategory.adapter = LogCategoryAdapter(it, this)
            }
        }

        //Log item recyclerview setup
        recyclerViewLogItem = binding.logItemRecyclerView
        recyclerViewLogItem.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        recyclerViewLogItem.adapter = LogItemListAdapter(this, sharedViewModel.selectedCategory.value?.emojiId?: 0)
        sharedViewModel.retrieveItemsByCategory(sharedViewModel.selectedCategory.value?.name.toString())
            .observe(this.viewLifecycleOwner) { items ->
                items.logItems.let {
                    (recyclerViewLogItem.adapter as LogItemListAdapter).submitList(it)
                }
            }
        recyclerViewLogItem.scrollToPosition(0)


        sharedViewModel.allLogItems.observe(this.viewLifecycleOwner) { items ->
            items.let { (recyclerViewLogItem.adapter as LogItemListAdapter).submitList(it) }
        }

        //Graph
        lineGraph = LineGraph(sharedViewModel.logValuesAndDates(), binding.logGraph)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCategoryClicked(logCategory: LogCategory) {
        if (sharedViewModel.setCategory(logCategory)) {

            //Notify adapters
            binding.logCategoryRecyclerView.adapter?.notifyDataSetChanged()
            binding.logItemRecyclerView.adapter?.notifyDataSetChanged()

            sharedViewModel.selectedCategory

            sharedViewModel.retrieveItemsByCategory(sharedViewModel.selectedCategory.value?.name.toString())
                .observe(this.viewLifecycleOwner) { items ->
                    items.logItems.let {
                        (binding.logItemRecyclerView.adapter as LogItemListAdapter).submitList(it)
                    }
                }
            lineGraph.setValues(sharedViewModel.logValuesAndDates())
        }
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