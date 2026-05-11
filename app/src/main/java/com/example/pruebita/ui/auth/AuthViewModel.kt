package com.example.pruebita.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebita.data.repository.AuthViewModel
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthViewModel()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password)
        }
    }
}