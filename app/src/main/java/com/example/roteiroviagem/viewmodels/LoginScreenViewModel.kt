package com.example.roteiroviagem.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginRegisterUser(
    val user : String = "",
    val email : String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

class LoginScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginRegisterUser())
    val uiState : StateFlow<LoginRegisterUser> = _uiState.asStateFlow()

    fun onUserChange(user:String) {
        _uiState.value = _uiState.value.copy(user = user)

    }

    fun onEmailChange(email: String){
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String){
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onConfirmPassword(confirm: String){
        _uiState.value = _uiState.value.copy(confirmPassword = confirm)
    }
}