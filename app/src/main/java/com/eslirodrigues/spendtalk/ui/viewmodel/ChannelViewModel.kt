package com.eslirodrigues.spendtalk.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.eslirodrigues.spendtalk.core.state.ChannelState
import com.eslirodrigues.spendtalk.data.model.Channel
import com.eslirodrigues.spendtalk.data.repository.ChannelRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChannelViewModel : ViewModel() {

    private val repository = ChannelRepository()

    val response: MutableState<ChannelState> = mutableStateOf(ChannelState.Empty)

    fun getChannels(currentUserEmail: String) = viewModelScope.launch {
        repository.getChannels(currentUserEmail)
            .onStart {
                response.value = ChannelState.Loading
            }
            .catch {
                response.value = ChannelState.Failure(it)
            }
            .collect {
                delay(40L)
                response.value = ChannelState.Success(it)
            }
    }

    fun addChannel(channel: Channel) = viewModelScope.launch {
        repository.addChannel(channel)
    }
}