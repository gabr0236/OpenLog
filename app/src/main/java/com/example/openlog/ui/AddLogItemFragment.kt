package com.example.openlog.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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

        //Log category recyclerview setup
        recyclerViewCategory = binding.recyclerView
        recyclerViewCategory.layoutManager =
            LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
        sharedViewModel.allLogCategories.observe(this.viewLifecycleOwner) { items ->
            items.let {
                recyclerViewCategory.adapter = LogCategoryAdapter(it, this)
            }
            sharedViewModel.setSelectedCategory(items.first())
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

        if (input.isBlank() || !isValidNumber(input)) return //Return if null or blank

        sharedViewModel.addNewLogItem(input, date)

        Toast.makeText(requireContext(), "Log Tilføjet", Toast.LENGTH_SHORT).show()
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
            .setTitle("Slet Kategori")
            .setMessage("Vil slette denne kategori? Denne handling vil medføre sletning af alle logge tilhørende denne kategori")
            .setIcon(R.drawable.emoji_warning)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _ ->
                //If yes is selected
                //Ask for confirmation
                AlertDialog.Builder(context)
                    .setTitle("Slet Kategori")
                    .setMessage("Er du sikker? Slettet Data kan ikke genskabes.")
                    .setIcon(R.drawable.emoji_warning)
                    .setPositiveButton(
                        android.R.string.yes
                    ) { _, _ ->
                        //If yes is selected
                        Toast.makeText(
                            context,
                            "Kategori Slettet",
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


    //TODO: duplicate method
    /**
     * Best suited solution if negative and positive number which can be formatted with '-' and '.'
     */
    private fun isValidNumber(s: String?) : Boolean {
        val regex = """^(-)?[0-9]{0,}((\.){1}[0-9]{1,}){0,1}$""".toRegex()
        return if (s.isNullOrEmpty()) false
        else if (s.contains(",")){
            Toast.makeText(requireContext(), "Brug punktum '.' i stedet for komma ','", Toast.LENGTH_LONG).show()
            false
        } else regex.matches(s)
    }
}