package com.eslirodrigues.spendtalk.core.state

import com.eslirodrigues.spendtalk.data.model.Message

sealed class MessageState {
    class Success(val data: List<Message>) : MessageState()
    class Failure(val msg: Throwable) : MessageState()
    object Loading : MessageState()
    object Empty : MessageState()
}
