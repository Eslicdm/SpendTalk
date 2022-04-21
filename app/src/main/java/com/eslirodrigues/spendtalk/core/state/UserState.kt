package com.eslirodrigues.spendtalk.core.state

import com.eslirodrigues.spendtalk.data.model.User

sealed class UserState {
    class Success(val data: List<User>) : UserState()
    class Failure(val msg: Throwable) : UserState()
    object Loading : UserState()
    object Empty : UserState()
}