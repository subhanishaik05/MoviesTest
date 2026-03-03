package com.example.myapplication.ui.viewmodels


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp

    fun onOtpChanged(value: String) {
        _otp.value = value
    }

    fun extractOtp(message: String?) {
        val regex = Regex("\\d{6}")
        val extracted = regex.find(message ?: "")?.value
        extracted?.let {
            _otp.value = it
        }
    }
}