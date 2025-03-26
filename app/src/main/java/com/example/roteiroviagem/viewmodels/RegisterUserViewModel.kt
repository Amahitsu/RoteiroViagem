package com.example.roteiroviagem.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class RegisterUser(
    val userRegister: String = "",
    val emailRegister : String = "",
    val passwordRegister: String = "",
    val confirmPassword: String = ""
)


class RegisterUserViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUser())
    val uiState : StateFlow<RegisterUser> = _uiState.asStateFlow()

    fun onRegisterUser(userRegister:String) {
        _uiState.value = _uiState.value.copy(userRegister = userRegister)

    }

    fun onRegisterEmail(emailRegister: String){
        _uiState.value = _uiState.value.copy(emailRegister = emailRegister)
    }

    fun onRegisterPassword(registerPassword: String){
        _uiState.value = _uiState.value.copy(passwordRegister = registerPassword)
    }

    fun onConfirmPassword(confirm: String){
        _uiState.value = _uiState.value.copy(confirmPassword = confirm)
    }
}