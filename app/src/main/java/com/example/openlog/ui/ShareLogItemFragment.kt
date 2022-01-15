package com.example.openlog.ui


import android.annotation.SuppressLint
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
            createFileAndIntent()

        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    fun createFileAndIntent() {
        Log.d("FILE", "Path of filesDir: ${context?.filesDir.toString()}")

        //Lav nyt subdirectory i **internal storage**
        val dir = File(context?.filesDir, "mydir")
        if (!dir.exists()) {
            dir.mkdir()
        }
        else dir.delete()
        Log.d("FILE", "Path of mydir: ${dir.absolutePath}")


        //Skriv fil til internal storage
        try {
            val gpxfile = File(dir, "OpenLog.csv")
            
            sharedViewModel.exportToCSV(gpxfile)

            Log.d("FILE", "Path of OpenLog.csv: ${gpxfile.absolutePath}")

            Log.d("FILE", "Try catch finish no exceptions")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val filePath= File(context?.filesDir, "mydir")
        Log.d("FILE", "Path of filePath (should be same as mydir): ${dir.absolutePath}")

        val newFile = File(filePath, "OpenLog.csv")
        Log.d("FILE", "Path of newFile (should be same as OpenLog.csv): ${newFile.absolutePath}")


        if (!newFile.exists() || !newFile.canRead()) throw IllegalArgumentException("newFile not exists")

        val fileURI = FileProvider.getUriForFile(
            context!!,
            context?.packageName + ".provider",
            newFile)

        if (Uri.EMPTY.equals(fileURI)) throw IllegalArgumentException("Uri not found")

        val sendIntent = Intent(Intent.ACTION_SEND)

        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "OpenLog")
        sendIntent.putExtra(Intent.EXTRA_TEXT, "body text")
        sendIntent.putExtra(Intent.EXTRA_STREAM, fileURI)


        val chooser = Intent.createChooser(sendIntent, "Share File")

        val resInfoList: List<ResolveInfo> = context!!.packageManager
            .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            context?.grantUriPermission(
                packageName,
                fileURI,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        startActivity(chooser)

    }
}