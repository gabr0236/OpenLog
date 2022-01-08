package com.example.openlog.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
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
import android.content.ClipData
import java.lang.IllegalArgumentException
import android.os.Environment
import android.util.Log
import java.io.FileWriter
import java.lang.Exception


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

    @SuppressLint("QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Log.d("TEST", context!!.getFilesDir().toString())
        //Log.d("TEST", context!!.getCacheDir().toString())
        //Log.d("TEST", Environment.getExternalStorageDirectory().toString())
        //Log.d("TEST", context!!.getExternalFilesDir(null).toString())
        //Log.d("TEST", context!!.getExternalCacheDir().toString())
        //Log.d("TEST", context!!.getExternalMediaDirs().toString())


        binding.shareAction.setOnClickListener {
            //lifecycleScope.launch {

            Log.d("FILE", "Path of filesDir: ${context!!.getFilesDir().toString()}")

            //Lav nyt subdirectory i **internal storage**
            val dir: File = File(context!!.filesDir, "mydir")
            if (!dir.exists()) {
                dir.mkdir()
            }
            Log.d("FILE", "Path of mydir: ${dir.absolutePath}")


            //Skriv fil til internal storage
            try {

                val gpxfile = File(dir, "testfile.txt")
                val writer = FileWriter(gpxfile)
                writer.append("This is a testfile")
                writer.flush()
                writer.close()
                Log.d("FILE", "Path of testfile.txt: ${gpxfile.absolutePath}")

                Log.d("FILE", "Try catch finish no exceptions")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //TODO ALL ABOVE WORKING PROPERLY

            val filePath= File(context!!.filesDir, "mydir")
            Log.d("FILE", "Path of filePath (should be same as mydir): ${dir.absolutePath}")

            val newFile = File(filePath, "testfile.txt")
            Log.d("FILE", "Path of newFile (should be same as testfile.txt): ${newFile.absolutePath}")


            if (!newFile.exists() || !newFile.canRead()) throw IllegalArgumentException("newFile not exists")



            val fileURI = FileProvider.getUriForFile(
                context!!,
                context!!.packageName + ".provider",
                newFile)

            if (Uri.EMPTY.equals(fileURI)) throw IllegalArgumentException("Uri not found")

            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            sendIntent.data=fileURI
            sendIntent.type = "*/*"

            startActivity(sendIntent)
          //  }
        }
    }
}