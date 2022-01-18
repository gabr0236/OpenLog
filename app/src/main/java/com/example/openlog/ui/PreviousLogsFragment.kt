package com.example.openlog.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.LogItemApplication
import com.example.openlog.R
import com.example.openlog.adapter.LogCategoryAdapter
import com.example.openlog.adapter.LogItemPagingAdapter
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.databinding.FragmentPreviousLogsBinding
import com.example.openlog.util.showCustomToast
import com.example.openlog.viewmodel.SharedViewModel
import com.example.openlog.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PreviousLogsFragment : DuplicateMethods(), OnItemClickListenerLogItem, CategoryRecyclerviewHandler {

    private var _binding: FragmentPreviousLogsBinding? = null
    private val binding get() = _binding!!
    private lateinit var lineGraph: LineGraph
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var recyclerViewLogItem: RecyclerView
    private lateinit var pagingAdapter: LogItemPagingAdapter

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
            if (items.any()) sharedViewModel.setSelectedCategory(items.first())
        }

        //Log item recyclerview setup
        recyclerViewLogItem = binding.logItemRecyclerView
        pagingAdapter = LogItemPagingAdapter(this, sharedViewModel.selectedCategory)
        recyclerViewLogItem.adapter=pagingAdapter

        sharedViewModel.selectedCategory.observe(this.viewLifecycleOwner) {
            lifecycleScope.launch {
                @OptIn(ExperimentalCoroutinesApi::class)
                sharedViewModel.logItems.collectLatest {
                    Log.d("TEST", "DATA CHANGED LOGITEMPAGINGADAPTER")
                    pagingAdapter.submitData(it)
                }
            }
        }

        //Observe data load state and provide viewmodel with snapshot of data when dat is loaded
        pagingAdapter.addLoadStateListener { loadState ->
            if (loadState.append.endOfPaginationReached)
            {
                sharedViewModel.setLastSnapshotLogItems(pagingAdapter.snapshot())
                updateFragmentView()
            }
        }
    }

    private fun updateFragmentView(){
        setRecyclerViewLogItemVisible()
        setSDAndAvg()
        lineGraph = LineGraph(sharedViewModel.logValuesAndDates(), binding.logGraph)
    }

    private fun setRecyclerViewLogItemVisible(){
        if (pagingAdapter.itemCount>=1){
            binding.logItemRecyclerView.visibility=View.VISIBLE
            binding.textviewNoLogsFound.visibility=View.INVISIBLE
        } else {
            binding.logItemRecyclerView.visibility=View.INVISIBLE
            binding.textviewNoLogsFound.visibility=View.VISIBLE
        }
    }

    private fun setSDAndAvg(){
        Log.d("TEST", sharedViewModel.mean().toString())
        binding.textviewAverage.text= getString(R.string.average, if(sharedViewModel.mean().toString().equals("NaN")) 0 else sharedViewModel.mean().toString())
        binding.textviewStandardDeviation.text= getString(R.string.standard_deviation, if(sharedViewModel.standdarddeviation().toString().equals("NaN")) 0 else sharedViewModel.standdarddeviation().toString())
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCategoryClicked(logCategory: LogCategory) {
        if (sharedViewModel.setSelectedCategory(logCategory)) {
            recyclerViewCategory.adapter?.notifyDataSetChanged()
        }
    }

    override fun onItemClickedFullLog(logItem: LogItem?) {
        sharedViewModel.setSelectedLogItemToEdit(logItem)
        findNavController().navigate(R.id.edit_log_fragment)
    }
}