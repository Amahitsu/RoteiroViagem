package com.example.roteiroviagem.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginUser(
    val user : String = "",
    val password: String = "",
)

class LoginScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUser())
    val uiState : StateFlow<LoginUser> = _uiState.asStateFlow()

    fun onUserChange(user:String) {
        _uiState.value = _uiState.value.copy(user = user)

    }

    fun onPasswordChange(password: String){
        _uiState.value = _uiState.value.copy(password = password)
    }

}