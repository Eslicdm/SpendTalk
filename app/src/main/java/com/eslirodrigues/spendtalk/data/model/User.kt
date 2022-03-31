package com.eslirodrigues.spendtalk.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val channels: List<Channel> = emptyList()
)