package com.example.openlog.ui


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.openlog.LogItemApplication
import com.example.openlog.databinding.FragmentShareLogsBinding
import com.example.openlog.viewmodel.SharedViewModel
import com.example.openlog.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import com.example.openlog.MainActivity

import androidx.core.content.FileProvider




class ShareLogItemFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        SharedViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    private var _binding: FragmentShareLogsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareLogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.shareAction.setOnClickListener {
            lifecycleScope.launch {
                val fileURI = FileProvider.getUriForFile(context!!,"com.codepath.fileprovider", File("src/main/java/com/example/openlog/item_logs.txt"))
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                sendIntent.putExtra(Intent.EXTRA_STREAM, fileURI)
                sendIntent.type = "*/*"
                startActivity(Intent.createChooser(sendIntent, "SHARE"))
            }
        }
    }
}