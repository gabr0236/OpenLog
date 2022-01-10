package com.example.openlog.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.LogItemApplication
import com.example.openlog.R
import com.example.openlog.adapter.LogCategoryAdapter
import com.example.openlog.adapter.LogItemAdapter
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
            updateFragmentView()
        }
        recyclerViewLogItem.scrollToPosition(0)

        //Observe logitems
        sharedViewModel.allLogItems.observe(this.viewLifecycleOwner) {
            items ->
            (recyclerViewLogItem.adapter as LogItemListAdapter).submitList(items.asSequence()
                ?.filter { log -> log.categoryOwnerName == sharedViewModel.selectedCategory.value?.name}
                ?.toList())
            updateFragmentView()
        }
    }

    private fun setRecyclerViewLogItemVisible(){
        val recyclerviewIsPopulated = sharedViewModel.anyLogsOfSelectedCategory()
        if (recyclerviewIsPopulated == true){
            binding.logItemRecyclerView.visibility=View.VISIBLE
            binding.textviewNoLogsFound.visibility=View.INVISIBLE
        } else {
            binding.logItemRecyclerView.visibility=View.INVISIBLE
            binding.textviewNoLogsFound.visibility=View.VISIBLE
        }
    }

    fun setSDAndAvg(){
        binding.textviewAverage.text= getString(R.string.average, sharedViewModel.mean().toString())
        binding.textviewStandardDeviation.text= getString(R.string.standard_deviation, sharedViewModel.standdarddeviation().toString())
    }

    fun updateFragmentView(){
        setSDAndAvg()
        setRecyclerViewLogItemVisible()
        lineGraph = LineGraph(sharedViewModel.logValuesAndDates(), binding.logGraph)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCategoryClicked(logCategory: LogCategory) {
        if (sharedViewModel.setSelectedCategory(logCategory)) {
            recyclerViewCategory.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateCategoryClicked() {
        findNavController().navigate(R.id.create_category_fragment)
    }

    override fun onDeleteCategoryClicked(logCategory: LogCategory) {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.delete_category))
            .setMessage(getString(R.string.delete_question))
            .setIcon(R.drawable.emoji_warning)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                //If yes is selected
                //Ask for confirmation
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.delete_category))
                    .setMessage(getString(R.string.delete_question_2))
                    .setIcon(R.drawable.emoji_warning)
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        //If yes is selected
                        Toast.makeText(
                            context,
                            getString(R.string.category_deleted),
                            Toast.LENGTH_SHORT
                        ).show()
                        sharedViewModel.deleteCategory(logCategory)
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .show()
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    override fun onItemClickedFullLog(logItem: LogItem) {
        sharedViewModel.setSelectedLogItemToEdit(logItem)
        findNavController().navigate(R.id.edit_log_fragment)
    }

    private fun show10MoreLogs(){
        //todo Database query add 10
    }

    private fun initLogItemAdapter(){
        val logItemAdapter = LogItemAdapter(this, sharedViewModel.selectedCategory, emptyList<LogItem>())
        recyclerViewLogItem.adapter = logItemAdapter
    }
}