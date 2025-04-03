package com.example.roteiroviagem.components

class EmailValidator {
    // Função para validar o formato do e-mail
    fun validate(email: String): String? {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

        return if (email.matches(emailPattern)) {
            null // E-mail válido
        } else {
            "Formato de e-mail inválido" // Retorna a mensagem de erro
        }
    }
}
