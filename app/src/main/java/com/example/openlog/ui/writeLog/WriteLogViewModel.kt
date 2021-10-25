package com.example.openlog.ui.writeLog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WriteLogViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is write log Fragment"
    }
    val text: LiveData<String> = _text
}