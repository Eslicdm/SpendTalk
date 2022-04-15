package com.eslirodrigues.spendtalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslirodrigues.spendtalk.data.model.User
import com.eslirodrigues.spendtalk.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    fun addUser(user: User) = viewModelScope.launch {
        repository.addUser(user)
    }
}