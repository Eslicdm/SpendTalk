package com.eslirodrigues.spendtalk.data.repository

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

    private val messageReference = database.getReference("message")

    fun getMessages() : Flow<List<Message>> = flow {
        val messageList: MutableList<Message> = emptyList<Message>().toMutableList()
        messageReference.keepSynced(true)
        messageReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.map { ds ->
                    ds.getValue(Message::class.java)?.copy(id = ds.key!!)
                }
                items.forEach {
                    messageList.add(it!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        emit(messageList)
    }.flowOn(Dispatchers.IO)


    fun sendMessage(message: Message) {
        messageReference.child(message.id)
            .setValue(Message(id = message.id, username = message.username, text = message.text))
    }
}