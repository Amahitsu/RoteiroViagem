package com.example.roteiroviagem.viewmodels

import androidx.lifecycle.ViewModel
import com.example.roteiroviagem.dao.UserDao

class RegisterUserViewModelFactory (
    private val userDao : UserDao
) : androidx.lifecycle.ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegisterUserViewModel(userDao) as T
    }
}
