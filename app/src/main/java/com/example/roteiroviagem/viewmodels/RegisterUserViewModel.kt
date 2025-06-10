package com.example.roteiroviagem.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roteiroviagem.components.EmailValidator
import com.example.roteiroviagem.dao.UserDao
import com.example.roteiroviagem.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


data class RegisterUiState(
    val userRegister: String = "",
    val nameRegister: String = "",
    val emailRegister: String = "",
    val emailError: String? = null,
    val passwordRegister: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val errorMessage: String = "",
    val isFormValid: Boolean = false, // 🔹 Adicionado para indicar se o formulário está válido
    val isSaved: Boolean = false
) {
    fun toUser(): User {
        return User(
            user = userRegister,
            name = nameRegister,
            email = emailRegister,
            password = passwordRegister
        )
    }
}

class RegisterUserViewModel(
    private val userDao: UserDao
) : ViewModel() {


    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onRegisterUser(user: String) {
        _uiState.value = _uiState.value.copy(userRegister = user)
        validateForm()
    }

    fun onRegisterName(name: String) {
        _uiState.value = _uiState.value.copy(nameRegister = name)
        validateForm()
    }

    fun onRegisterEmail(email: String) {
        val emailValidator = EmailValidator()
        val emailError = emailValidator.validate(email)

        _uiState.value = _uiState.value.copy(
            emailRegister = email,
            emailError = emailError // <- atualiza o estado com o erro
        )

        validateForm()
    }


    fun onRegisterPassword(password: String) {
        _uiState.value = _uiState.value.copy(passwordRegister = password)
        validateForm()
    }

    fun onConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
        // Não validamos o erro aqui para evitar que ele apareça enquanto a pessoa digita
    }

    fun onConfirmPasswordFocusLost() {
        val currentPassword = _uiState.value.passwordRegister
        val confirmPassword = _uiState.value.confirmPassword

        val passwordError = if (confirmPassword.isNotEmpty() && currentPassword.isNotEmpty()) {
            if (confirmPassword == currentPassword) null else "As senhas não coincidem"
        } else {
            null // Se um dos campos estiver vazio, não exibe erro
        }

        _uiState.value = _uiState.value.copy(passwordError = passwordError)
        validateForm()
    }

    private fun validateForm() {
        _uiState.value = _uiState.value.copy(
            isFormValid = _uiState.value.run
            {
                userRegister.isNotBlank() &&
                        nameRegister.isNotBlank() &&
                        emailRegister.isNotBlank() &&
                        emailError == null &&
                        passwordRegister.isNotBlank() &&
                        confirmPassword.isNotBlank() &&
                        passwordError == null
            }
        )
    }

    fun register() {
        try {

            viewModelScope.launch {
                userDao.insert(_uiState.value.toUser())
                _uiState.value = _uiState.value.copy(isSaved = true)
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(errorMessage = e.message ?: "Unknow error")
        }
    }

    fun cleanDisplayValues() {
        _uiState.value = _uiState.value.copy(isSaved = false, errorMessage = "")
    }
}