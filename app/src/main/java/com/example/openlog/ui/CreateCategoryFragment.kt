package com.example.openlog.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.openlog.LogItemApplication
import com.example.openlog.R
import com.example.openlog.adapter.EmojiArrayAdapter
import com.example.openlog.databinding.FragmentCreateCategoryBinding
import com.example.openlog.util.EmojiRetriever
import com.example.openlog.util.showCustomToast
import com.example.openlog.viewmodel.SharedViewModel
import com.example.openlog.viewmodel.SharedViewModelFactory
import java.util.*

class CreateCategoryFragment : Fragment() {
    private val sharedViewModel: SharedViewModel by activityViewModels {
        val db = (activity?.application as LogItemApplication).database

        SharedViewModelFactory(
            db.logItemDao(),
            db.logCategoryDao()
        )
    }

    private var _binding: FragmentCreateCategoryBinding? = null
    private val binding get() = _binding!!
    private var emojiArrayAdapter: EmojiArrayAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val createCategoryFragmentBinding: FragmentCreateCategoryBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_category, container, false)
        createCategoryFragmentBinding.createCategoryFragment = this
        _binding = createCategoryFragmentBinding
        return createCategoryFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Databinding
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            createCategoryFragment = this@CreateCategoryFragment
            viewModel = sharedViewModel
        }

        emojiArrayAdapter =
            context?.let { EmojiArrayAdapter(it, EmojiRetriever.getEmojiIdIndexes()) }
        binding.spinnerEmojis.adapter = emojiArrayAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    fun createCategory() {
        val name = binding.textInputCategoryName.text
        val unit = binding.textInputCategoryUnit.text
        if (unit.isNullOrBlank() || name.isNullOrBlank()) return //return if no input

        //disallow duplicate names
        if (sharedViewModel.allLogCategories.value?.any { lc -> lc.name == name.toString() } == true) {
            name.clear()
            unit.clear()
            Toast(context).showCustomToast(
                getString(R.string.category_exists),
                R.drawable.emoji_x,
                true,
                requireActivity()
            )
            return
        }
        sharedViewModel.createCategory(
            name.toString(),
            unit.toString(),
            binding.spinnerEmojis.selectedItem.toString().toInt()
        )

        Toast(context).showCustomToast(
            getString(R.string.category_has_been_created),
            R.drawable.emoji_checkmark,
            true,
            requireActivity()
        )

        findNavController().popBackStack()
    }
}