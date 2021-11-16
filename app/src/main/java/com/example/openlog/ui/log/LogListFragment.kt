package com.example.openlog.ui.log

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openlog.LogApplication
import com.example.openlog.R
import com.example.openlog.adapter.LogListAdapter
import com.example.openlog.databinding.FragmentLogListBinding
import com.example.openlog.model.LogViewModel
import com.example.openlog.model.LogViewModelFactory

class LogListFragment : Fragment() {
    private val viewModel: LogViewModel by activityViewModels {
        val db = (activity?.application as LogApplication).database

        LogViewModelFactory(
            db.categoryDao(),
            db.logDao()
        )
    }

    private var _binding: FragmentLogListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = LogListAdapter {
            // Do something
        }
        binding.recyclerView.adapter = adapter
        viewModel.allLogs.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
    }
}