package com.eslirodrigues.spendtalk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Channel(
    val id: String = "",
    val creatorEmail: String = "",
    val friendEmail: String = "",
    val hasMessage: Boolean = false,
) : Parcelable
