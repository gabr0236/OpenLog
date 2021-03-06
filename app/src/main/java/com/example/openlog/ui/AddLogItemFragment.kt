package com.example.openlog.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.R
import com.example.openlog.adapter.LogCategoryAdapter
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.databinding.FragmentAddLogBinding
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.util.EmojiRetriever
import com.example.openlog.util.InputValidator
import com.example.openlog.util.showCustomToast
import java.util.*


class AddLogItemFragment : DuplicateMethods(), CategoryRecyclerviewHandler {

    private var _binding: FragmentAddLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewCategory: RecyclerView

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
        val addLogItemLayoutBinding: FragmentAddLogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_log, container, false)
        addLogItemLayoutBinding.addLogItemFragment = this
        addLogItemLayoutBinding.duplicateMethods = this
        _binding = addLogItemLayoutBinding

        return addLogItemLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Databinding
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            addLogItemFragment = this@AddLogItemFragment
            viewModel = sharedViewModel
        }

        //Setup for speech to text
        setupSpeechToText()
        microphoneButton = binding.buttonMicrophone
        microphoneButton.setOnClickListener {
            checkAudioPermission()
            ////Toggle listening
            speechRecognizer.startListening(speechRecognizerIntent)
            microphoneButton.setColorFilter(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.mic_enabled_color
                )
            )
        }

        binding.buttonEditDate.setOnClickListener { pickDateTime(binding.textDate) }
        //Log category recyclerview setup
        recyclerViewCategory = binding.recyclerView
        recyclerViewCategory.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        sharedViewModel.allLogCategories.observe(this.viewLifecycleOwner) { items ->
            items.let {
                recyclerViewCategory.adapter = LogCategoryAdapter(it, this)
            }
            if (!items.isNullOrEmpty()) sharedViewModel.setSelectedCategory(items.first())
        }

        this.setDate(Calendar.getInstance().time) //Show current date on screen
        this.getDate().let {
            binding.textDate.text =
                it?.let { it1 -> DateTimeFormatter.formatAsYearDayDateTime(it1) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    fun addNewLogItem() {
        val input = binding.logValue.text.toString()
        binding.logValue.text?.clear()

        if (!InputValidator.isValidNumber(
                requireContext(),
                requireActivity(),
                input
            )
        ) return //Return if not valid input

        sharedViewModel.addNewLogItem(input, this.getDate())

        sharedViewModel.selectedCategory.value?.emojiId?.let { EmojiRetriever.getEmojiIDOf(it) }
            ?.let {
                Toast(context).showCustomToast(
                    getString(R.string.log_added),
                    it,
                    true,
                    requireActivity()
                )
            }

        this.setDate(null)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCategoryClicked(logCategory: LogCategory) {
        if (sharedViewModel.setSelectedCategory(logCategory)) {
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun checkAudioPermission() {
        val permission = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.RECORD_AUDIO
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast(context).showCustomToast(
                        getString(R.string.permission_denied),
                        R.drawable.emoji_x,
                        true,
                        requireActivity()
                    )

                } else {
                    Toast(context).showCustomToast(
                        getString(R.string.permission_granted),
                        R.drawable.emoji_x,
                        true,
                        requireActivity()
                    )
                }
            }
        }
    }

    private fun setupSpeechToText() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
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
                microphoneButton.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.mic_disabled_color
                    )
                )
                Log.i("TEST", "Speech recognition ended")
            }

            override fun onError(i: Int) {
                microphoneButton.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.mic_disabled_color
                    )
                )
                if (!listening && i == SpeechRecognizer.ERROR_NO_MATCH) return
                if (i == SpeechRecognizer.ERROR_NO_MATCH) return
                Log.e("TEST", "Speech recognition error: " + i)
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