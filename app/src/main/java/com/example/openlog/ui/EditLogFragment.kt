package com.example.openlog.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.LogItemApplication
import com.example.openlog.R
import com.example.openlog.adapter.LogCategoryAdapter
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.data.entity.LogItem
import com.example.openlog.databinding.FragmentEditLogBinding
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.util.EmojiRetriever
import com.example.openlog.util.InputValidator
import com.example.openlog.util.showCustomToast
import com.example.openlog.viewmodel.SharedViewModel
import com.example.openlog.viewmodel.SharedViewModelFactory
import java.util.*

class EditLogFragment : DuplicateMethods(), CategoryRecyclerviewHandler {

    private var _binding: FragmentEditLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewCategory: RecyclerView

    private lateinit var logItem: LogItem

    //Speech to text
    private lateinit var microphoneButton: ImageView
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private var listening = false
    private val RECORD_REQUEST_CODE = 101


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editLogLayoutBinding: FragmentEditLogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_log, container, false)
        editLogLayoutBinding.editLogFragment = this
        _binding = editLogLayoutBinding
        return editLogLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Databinding
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            editLogFragment = this@EditLogFragment
            viewModel = sharedViewModel
        }
        binding.buttonEditDate.setOnClickListener{pickDateTime(binding.textDate)}

        //Setup for speech to text
        setupSpeechToText()
        microphoneButton = binding.buttonMicrophone
        microphoneButton.setOnClickListener {
            checkAudioPermission()
            ////Toggle listening
            speechRecognizer.startListening(speechRecognizerIntent)
            microphoneButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.mic_enabled_color))
        }

        //Log category recyclerview setup
        recyclerViewCategory = binding.recyclerView
        recyclerViewCategory.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        sharedViewModel.allLogCategories.observe(this.viewLifecycleOwner) { items ->
            items.let {
                recyclerViewCategory.adapter = LogCategoryAdapter(it, this)
            }
        }

        sharedViewModel.selectedLogItemToEdit.value
            ?: return //return if selectedLogItemToEdit is null
        logItem = sharedViewModel.selectedLogItemToEdit.value!!

        binding.logValue.setText(logItem.value.toString())
        binding.textDate.text =
            logItem.date?.let { DateTimeFormatter.formatAsYearDayDateTime(it) } //Date of log
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    fun updateLogItem() {
        val input = binding.logValue.text.toString()
        if (!InputValidator.isValidNumber(requireContext(),requireActivity(),input)) {
            binding.logValue.setText(logItem.value.toString())
            return
        }
        sharedViewModel.updateLogItem(
            logItem.id,
            input,
            this.getDate() ?: logItem.date
        )
        binding.logValue.text?.clear()
        this.setDate(null)

        sharedViewModel.selectedCategory.value?.emojiId?.let { EmojiRetriever.getEmojiIDOf(it) }?.let {
            Toast(context).showCustomToast(
                getString(R.string.log_has_been_updated),
                it,
                true,
                requireActivity())
        }

        findNavController().navigate(R.id.previous_logs_fragment)
    }

    fun deleteLog() {
        //Confirmation dialog
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.delete_log))
            .setMessage(getString(R.string.delete_question))
            .setIcon(R.drawable.emoji_warning)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                //If yes is selected
                Toast(context).showCustomToast(getString(R.string.log_deleted), R.drawable.emoji_checkmark, true, requireActivity())

                sharedViewModel.deleteLogItem(logItem)
                findNavController().navigate(R.id.previous_logs_fragment)
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCategoryClicked(logCategory: LogCategory) {
        if (sharedViewModel.setSelectedCategory(logCategory)) {
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun checkAudioPermission() {
        val permission = ContextCompat.checkSelfPermission(context!!,
            Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast(context).showCustomToast(getString(R.string.permission_denied), R.drawable.emoji_x, true, requireActivity())

                } else {
                    Toast(context).showCustomToast(getString(R.string.permission_granted), R.drawable.emoji_x, true, requireActivity())
                }
            }
        }
    }

    private fun setupSpeechToText() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        Log.d("Language", "${Locale.getDefault()}")

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(bundle: Bundle?) {
                listening = true
                Log.i("TEST", "Ready for speech input")
            }
            override fun onBeginningOfSpeech() {
                Log.i("TEST", "Speech beginning")
            }

            override fun onEndOfSpeech() {
                listening = false
                // changing the color of our mic icon to
                // gray to indicate it is not listening
                microphoneButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.mic_disabled_color))
                Log.i("TEST", "Speech recognition ended")
            }
            override fun onError(i: Int) {
                if (!listening && i == SpeechRecognizer.ERROR_NO_MATCH) return
                if (i == SpeechRecognizer.ERROR_NO_MATCH) return
                Log.e("TEST","Speech recognition error: " + i)

            }

            override fun onResults(bundle: Bundle) {
                val result = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d("TEST", "Result of text to speech: ${result?.get(0)}")

                if (result != null) {
                    // result[0] will give the output of speech
                    binding.logValue.setText(result[0].toString())
                    Log.d("TEST", "Result of text to speech: ${result[0]}")
                }
            }
            override fun onRmsChanged(p0: Float) {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onPartialResults(p0: Bundle?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
        })
    }
}