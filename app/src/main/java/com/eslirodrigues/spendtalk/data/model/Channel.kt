package com.eslirodrigues.spendtalk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Channel(
    val id: String = "",
    val creatorEmail: String = "",
    val friendEmail: String = "",
    val messages: HashMap<String, Message> = hashMapOf(),
    val hasMessage: Boolean = false,
) : Parcelable
