package com.eslirodrigues.spendtalk.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.eslirodrigues.spendtalk.data.model.Channel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ChannelRepository {

    private val database = Firebase.database

    private val channelReference = database.getReference("channel")

    fun getChannels() : Flow<List<Channel>> = flow {
        val channelList = mutableStateListOf<Channel>()
        channelReference.keepSynced(true)
        channelReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map { ds ->
                    ds.getValue(Channel::class.java)?.copy(id = ds.key!!)
                }.forEach { channel ->
                    channelList.add(channel!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        emit(channelList)
    }.flowOn(Dispatchers.IO)


    fun addChannel(channel: Channel) {
        channelReference.child(channel.id)
            .setValue(
                Channel(
                    id = channel.id,
                    creatorEmail = channel.creatorEmail,
                    friendEmail = channel.friendEmail,
                    hasMessage = channel.hasMessage
                )
            )
    }

    fun deleteChannel(channel: Channel) {
        channelReference.child(channel.id).removeValue()
    }
}