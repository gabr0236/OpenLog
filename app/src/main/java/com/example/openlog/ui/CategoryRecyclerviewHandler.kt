package com.example.openlog.ui

import com.example.openlog.data.entity.LogCategory

interface CategoryRecyclerviewHandler {
    fun onCategoryClicked(logCategory: LogCategory)
    fun onCreateCategoryClicked()
    fun onDeleteCategoryClicked()
}