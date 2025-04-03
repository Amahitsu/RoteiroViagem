package com.example.roteiroviagem.viewmodels

import androidx.lifecycle.ViewModel
import com.example.roteiroviagem.components.EmailValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


data class RegisterUiState(
    val userRegister: String = "",
    val nameRegister: String = "",
    val emailRegister: String = "",
    val passwordRegister: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val isFormValid: Boolean = false // 🔹 Adicionado para indicar se o formulário está válido
)

class RegisterUserViewModel : ViewModel() {
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

        // Validando o e-mail com a classe separada
        val emailError = emailValidator.validate(email)

        // Atualizando o estado com o e-mail e o erro
        _uiState.value = _uiState.value.copy(
            emailRegister = email
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
            isFormValid = _uiState.value.run {
                userRegister.isNotBlank() &&
                        nameRegister.isNotBlank() &&
                        emailRegister.isNotBlank() &&
                        passwordRegister.isNotBlank() &&
                        confirmPassword.isNotBlank() &&
                        passwordError == null
            }
        )
    }
}