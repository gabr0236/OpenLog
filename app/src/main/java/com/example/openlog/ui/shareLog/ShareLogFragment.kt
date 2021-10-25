package com.example.openlog.ui.shareLog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.openlog.databinding.FragmentShareLogBinding

class ShareLogFragment : Fragment() {

    private lateinit var shareLogViewModel: ShareLogViewModel
    private var _binding: FragmentShareLogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shareLogViewModel =
            ViewModelProvider(this).get(ShareLogViewModel::class.java)

        _binding = FragmentShareLogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textShareLog
        shareLogViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}