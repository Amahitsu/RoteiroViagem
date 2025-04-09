package com.example.roteiroviagem.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roteiroviagem.dao.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUser(
    val user: String = "",
    val password: String = "",
    val errorMessage: String = "",
    val isLoggedIn: Boolean = false
)

class LoginScreenViewModel(private val registerUserDao: UserDao) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUser())
    val uiState: StateFlow<LoginUser> = _uiState.asStateFlow()

    fun onUserChange(value: String) {
        _uiState.value = _uiState.value.copy(user = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun login() {
        viewModelScope.launch {
            val user = registerUserDao.findByUsername(_uiState.value.user)
            if (user == null) {
                _uiState.value = _uiState.value.copy(errorMessage = "Usuário não encontrado")
            } else if (user.password != _uiState.value.password) {
                _uiState.value = _uiState.value.copy(errorMessage = "Senha incorreta")
            } else {
                _uiState.value = _uiState.value.copy(isLoggedIn = true)
                Log.d("Login", "Usuário logado com sucesso: ${user.user}")
            }
        }
    }

    fun cleanErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = "")
    }
}
