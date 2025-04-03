package com.example.roteiroviagem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun MyTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier // Adiciona modifier como parâmetro opcional
) {
    var isTouched = remember {
        mutableStateOf(false)
    }
    var focusRequester = remember {
        FocusRequester()
    }

    // Se for um campo de senha, aplica a transformação de senha
    if (isPassword) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            label = { Text(text = label) },
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusEvent {
                    if (it.hasFocus) isTouched.value = true
                },
            isError = isTouched.value && value.isBlank(),
            supportingText = {
                if (isTouched.value && value.isBlank()) {
                    Text(text = "Campo ${label} Obrigatório")
                }
            }
        )
    } else {
        OutlinedTextField(
            value = value,
            onValueChange = {
                isTouched.value = true
                onValueChange(it)
            },
            visualTransformation = visualTransformation,
            singleLine = true,
            label = { Text(text = label) },
            modifier = modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusEvent {
                    if (it.hasFocus) isTouched.value = true
                },
            isError = isTouched.value && value.isBlank(),
            supportingText = {
                if (isTouched.value && value.isBlank()) {
                    Text(text = "Campo ${label} Obrigatório")
                }
            }
        )
    }
}
