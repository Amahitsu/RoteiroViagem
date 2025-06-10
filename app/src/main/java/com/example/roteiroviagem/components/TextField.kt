package com.example.roteiroviagem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
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
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val isTouched = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val showError = (isTouched.value && value.isBlank()) || errorMessage != null

    val supportingTextMessage = when {
        isTouched.value && value.isBlank() -> "Campo $label obrigatório"
        errorMessage != null -> errorMessage
        else -> ""
    }

    OutlinedTextField(
        value = value,
        onValueChange = {
            isTouched.value = true
            onValueChange(it)
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else visualTransformation,
        singleLine = true,
        label = { Text(text = label) },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusEvent {
                if (it.hasFocus) isTouched.value = true
            },
        isError = showError,
        supportingText = {
            if (showError) {
                Text(
                    text = supportingTextMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    )
}
