package com.example.openlog.ui.writeLog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.openlog.databinding.FragmentWriteLogBinding

class WriteLogFragment : Fragment() {

    private lateinit var writeLogViewModel: WriteLogViewModel
    private var _binding: FragmentWriteLogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        writeLogViewModel =
            ViewModelProvider(this).get(WriteLogViewModel::class.java)

        _binding = FragmentWriteLogBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}