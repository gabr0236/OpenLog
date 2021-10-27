package com.example.openlog.ui.writeLog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.adapter.CategoryAdapter
import com.example.openlog.databinding.FragmentWriteLogBinding

class WriteLogFragment : Fragment() {

    private var _binding: FragmentWriteLogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWriteLogBinding.inflate(inflater, container, false)

        recyclerView = binding.categories
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = CategoryAdapter(arrayOf("TEST1","TEST2","TEST3","TEST4","TEST5"))
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}