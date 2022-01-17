package com.example.openlog.ui

import com.example.openlog.data.entity.LogCategory
import com.example.openlog.viewmodel.SharedViewModel

interface CategoryRecyclerviewHandler {
    fun onCategoryClicked(logCategory: LogCategory)
    fun onCreateCategoryClicked()
    fun onDeleteCategoryClicked(logCategory: LogCategory)
}