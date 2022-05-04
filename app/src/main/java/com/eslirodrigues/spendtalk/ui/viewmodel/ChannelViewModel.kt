package com.eslirodrigues.spendtalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslirodrigues.spendtalk.core.state.ChannelState
import com.eslirodrigues.spendtalk.data.model.Channel
import com.eslirodrigues.spendtalk.data.repository.ChannelRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChannelViewModel : ViewModel() {

    private val repository = ChannelRepository()

    private val _channelState = MutableStateFlow<ChannelState>(ChannelState.Empty)
    val channelState: StateFlow<ChannelState> get() = _channelState

    init {
        getChannels()
    }


    private fun getChannels() = viewModelScope.launch {
        repository.getChannels()
            .onStart {
                _channelState.value = ChannelState.Loading
            }
            .catch {
                _channelState.value = ChannelState.Failure(it)
            }
            .collect {
                _channelState.value = ChannelState.Success(it)
            }
    }

    fun refreshChannels() {
        getChannels()
    }

    fun addChannel(channel: Channel) = viewModelScope.launch {
        repository.addChannel(channel)
    }

    fun deleteChannel(channel: Channel) = viewModelScope.launch {
        repository.deleteChannel(channel)
    }
}