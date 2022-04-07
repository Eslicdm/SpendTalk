package com.eslirodrigues.spendtalk.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.eslirodrigues.spendtalk.data.model.Channel
import com.eslirodrigues.spendtalk.data.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MessageRepository {

    private val database = Firebase.database

    private val messageReference = database.getReference("channel")

    fun getMessages(channel: Channel) : Flow<List<Message>> = flow {
        val messageList = mutableStateListOf<Message>()
        messageReference.keepSynced(true)
        messageReference.child(channel.id).child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map { ds ->
                    ds.getValue(Message::class.java)?.copy(id = ds.key!!)
                }.forEach { message ->
                    messageList.add(message!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        emit(messageList)
    }.flowOn(Dispatchers.IO)


    fun sendMessage(message: Message) {
        messageReference.child(message.channelId).child("messages").child(message.id)
            .setValue(Message(id = message.id, channelId = message.channelId, email = message.email, text = message.text))
    }
}