package com.eslirodrigues.spendtalk.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
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

    private val _userState: MutableState<UserState> = mutableStateOf(UserState.Empty)
    val userState: State<UserState> get() = _userState

    init {
        getUsers()
    }

    private fun getUsers() = viewModelScope.launch {
        repository.getUsers()
            .onStart {
                _userState.value = UserState.Loading
            }
            .catch {
                _userState.value = UserState.Failure(it)
            }
            .collect {
                delay(40L)
                _userState.value = UserState.Success(it)
            }
    }

    fun addUser(user: User) = viewModelScope.launch {
        repository.addUser(user)
    }
}