package com.example.openlog.ui


import android.R.attr.mimeType
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.openlog.LogItemApplication
import com.example.openlog.databinding.FragmentShareLogsBinding
import com.example.openlog.viewmodel.SharedViewModel
import com.example.openlog.viewmodel.SharedViewModelFactory
import java.io.File
import java.io.FileWriter


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

            val filePath= File(context!!.filesDir, "mydir")
            Log.d("FILE", "Path of filePath (should be same as mydir): ${dir.absolutePath}")

            val newFile = File(filePath, "testfile.txt")
            Log.d("FILE", "Path of newFile (should be same as testfile.txt): ${newFile.absolutePath}")


            if (!newFile.exists() || !newFile.canRead()) throw IllegalArgumentException("newFile not exists")

            //TODO ALL ABOVE WORKING PROPERLY
            //Somehow the mail is not being sent

            val fileURI = FileProvider.getUriForFile(
                context!!,
                context!!.packageName + ".provider",
                newFile)

            if (Uri.EMPTY.equals(fileURI)) throw IllegalArgumentException("Uri not found")

            val sendIntent = Intent(Intent.ACTION_SEND)

            sendIntent.setType("*/*")
            sendIntent.putExtra(Intent.EXTRA_EMAIL, "email@example.com")
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here")
            sendIntent.putExtra(Intent.EXTRA_TEXT, "body text")

            sendIntent.putExtra(Intent.EXTRA_STREAM, fileURI)
            sendIntent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            val chooser = Intent.createChooser(sendIntent, "Chooser Title")
            chooser.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            context!!.startActivity(chooser)

          //  }
        }
    }
}