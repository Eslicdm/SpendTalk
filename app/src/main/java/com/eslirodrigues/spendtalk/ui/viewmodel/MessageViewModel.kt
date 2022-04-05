package com.eslirodrigues.spendtalk.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.eslirodrigues.spendtalk.core.state.MessageState
import com.eslirodrigues.spendtalk.data.model.Channel
import com.eslirodrigues.spendtalk.data.model.Message
import com.eslirodrigues.spendtalk.data.repository.MessageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val repository = MessageRepository()

    val response: MutableState<MessageState> = mutableStateOf(MessageState.Empty)

    fun getMessages(channel: Channel) = viewModelScope.launch {
        repository.getMessages(channel)
            .onStart {
                response.value = MessageState.Loading
            }
            .catch {
                response.value = MessageState.Failure(it)
            }
            .collect {
                delay(40L)
                response.value = MessageState.Success(it)
            }
    }

    fun sendMessage(message: Message) = viewModelScope.launch {
        repository.sendMessage(message)
    }
}