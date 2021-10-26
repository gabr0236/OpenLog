package com.example.openlog.ui.shareLog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareLogViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This should probably be a popup, not a fragment"
    }
    val text: LiveData<String> = _text
}