package com.eslirodrigues.spendtalk.core.state

import com.eslirodrigues.spendtalk.data.model.Channel

sealed class ChannelState {
    class Success(val data: List<Channel>) : ChannelState()
    class Failure(val msg: Throwable) : ChannelState()
    object Loading : ChannelState()
    object Empty : ChannelState()
}