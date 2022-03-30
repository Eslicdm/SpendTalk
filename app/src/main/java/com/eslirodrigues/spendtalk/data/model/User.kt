package com.eslirodrigues.spendtalk.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val friends: List<User> = emptyList()
)