package com.eslirodrigues.spendtalk.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.eslirodrigues.spendtalk.core.state.MessageState
import com.eslirodrigues.spendtalk.data.model.Message
import com.eslirodrigues.spendtalk.data.repository.MessageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val repository = MessageRepository()

    val response: MutableState<MessageState> = mutableStateOf(MessageState.Empty)

    init {
        getAllMessages()
    }

    fun getAllMessages() = viewModelScope.launch {
        repository.getMessages()
            .onStart {
                response.value = MessageState.Loading
            }
            .catch {
                response.value = MessageState.Failure(it)
            }
            .onEach {

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