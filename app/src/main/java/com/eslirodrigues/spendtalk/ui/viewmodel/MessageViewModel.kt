package com.eslirodrigues.spendtalk.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eslirodrigues.spendtalk.core.state.MessageState
import com.eslirodrigues.spendtalk.data.model.Message
import com.eslirodrigues.spendtalk.data.repository.MessageRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {

    private val repository = MessageRepository()

    private val _messageState: MutableState<MessageState> = mutableStateOf(MessageState.Empty)
    val messageState: State<MessageState> get() = _messageState

    init {
        getMessages()
    }

    private fun getMessages() = viewModelScope.launch {
        repository.getMessages()
            .onStart {
                _messageState.value = MessageState.Loading
            }
            .catch {
                _messageState.value = MessageState.Failure(it)
            }
            .collect {
                _messageState.value = MessageState.Success(it)
            }
    }

    fun sendMessage(message: Message) = viewModelScope.launch {
        repository.sendMessage(message)
    }
}