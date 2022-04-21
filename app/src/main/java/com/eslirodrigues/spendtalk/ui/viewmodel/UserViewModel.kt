package com.eslirodrigues.spendtalk.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslirodrigues.spendtalk.core.state.UserState
import com.eslirodrigues.spendtalk.data.model.User
import com.eslirodrigues.spendtalk.data.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    val response: MutableState<UserState> = mutableStateOf(UserState.Empty)

    fun getUser(userEmail: String) = viewModelScope.launch {
        repository.getUser(userEmail)
            .onStart {
                response.value = UserState.Loading
            }
            .catch {
                response.value = UserState.Failure(it)
            }
            .collect {
                delay(40L)
                response.value = UserState.Success(it)
            }
    }

    fun addUser(user: User) = viewModelScope.launch {
        repository.addUser(user)
    }
}