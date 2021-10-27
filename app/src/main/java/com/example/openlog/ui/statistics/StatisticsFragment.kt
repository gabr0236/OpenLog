package com.example.openlog.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.adapter.CategoryAdapter
import com.example.openlog.adapter.PreviousLogAdapter
import com.example.openlog.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var recyclerViewPreviousLogs: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerViewCategories = binding.categories
        recyclerViewCategories.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerViewCategories.adapter = CategoryAdapter(arrayOf("TEST1","TEST2","TEST3","TEST4","TEST5"))

        recyclerViewPreviousLogs = binding.previousLogs
        recyclerViewPreviousLogs.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerViewPreviousLogs.adapter = PreviousLogAdapter(arrayOf("TEST1","TEST2","TEST3","TEST4","TEST5"))


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}