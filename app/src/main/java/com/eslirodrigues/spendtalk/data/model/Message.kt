package com.eslirodrigues.spendtalk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val id: String = "",
    val channelId: String = "",
    val email: String = "",
    val text: String = ""
) : Parcelable