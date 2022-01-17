package com.example.openlog.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.openlog.LogItemApplication
import com.example.openlog.R
import com.example.openlog.adapter.LogCategoryAdapter
import com.example.openlog.data.entity.LogCategory
import com.example.openlog.databinding.FragmentAddLogBinding
import com.example.openlog.util.DateTimeFormatter
import com.example.openlog.util.InputValidator
import com.example.openlog.viewmodel.SharedViewModel
import com.example.openlog.viewmodel.SharedViewModelFactory
import java.util.*


class AddLogItemFragment : Fragment(), CategoryRecyclerviewHandler {
    private val sharedViewModel: SharedViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        SharedViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    private var _binding: FragmentAddLogBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerViewCategory: RecyclerView
    private var date: Date? = null

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
            if (!items.isNullOrEmpty()) sharedViewModel.setSelectedCategory(items.first())
        }

        date = Calendar.getInstance().time //Show current date on screen
        date?.let { binding.textDate.text = DateTimeFormatter.formatAsYearDayDateTime(it) }
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

        if (!InputValidator.isValidNumber(requireContext(),input)) return //Return if not valid input

        sharedViewModel.addNewLogItem(input, date)

        Toast.makeText(requireContext(), getString(R.string.log_added), Toast.LENGTH_SHORT).show()
        date = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCategoryClicked(logCategory: LogCategory) {
        if (sharedViewModel.setSelectedCategory(logCategory)) {
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateCategoryClicked() {
        findNavController().navigate(R.id.create_category_fragment)
    }

    override fun onDeleteCategoryClicked(logCategory: LogCategory) {
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.delete_category))
            .setMessage(getString(R.string.delete_question))
            .setIcon(R.drawable.emoji_warning)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                //If yes is selected
                //Ask for confirmation
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.delete_category))
                    .setMessage(getString(R.string.delete_question_2))
                    .setIcon(R.drawable.emoji_warning)
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        //If yes is selected
                        Toast.makeText(
                            context,
                            getString(R.string.category_deleted),
                            Toast.LENGTH_SHORT
                        ).show()
                        sharedViewModel.deleteCategory(logCategory)
                    }
                    .setNegativeButton(android.R.string.no, null)
                    .show()
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    //TODO: lav i anden klasse så denne metode ikke skrives i 2 fragments
    fun pickDateTime() {
        Log.d("TEST", "PickDateTime clicked")
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(), { _, year, month, day ->
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year, month, day, hour, minute)
                date = pickedDateTime.time
                date?.let { binding.textDate.text = DateTimeFormatter.formatAsYearDayDateTime(it) }
                Log.d("TEST", "PickDateTime: $pickedDateTime")
            }, startHour, startMinute, true).show()
        }, startYear, startMonth, startDay).show()
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

                    Toast.makeText(context,"Permission Denied",Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context,"Permission Granted",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun setupSpeechToText() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
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
                microphoneButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.mic_disabled_color))
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