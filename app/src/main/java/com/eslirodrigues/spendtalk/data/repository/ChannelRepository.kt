package com.eslirodrigues.spendtalk.data.repository

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

    fun getChannels(currentUserEmail: String) : Flow<List<Channel>> = flow {
        val channelList: MutableList<Channel> = emptyList<Channel>().toMutableList()
        channelReference.keepSynced(true)
        channelReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.map { ds ->
                    ds.getValue(Channel::class.java)?.copy(id = ds.key!!)
                }
                items.forEach { channel ->
                    if (channel?.creatorEmail == currentUserEmail || channel?.friendEmail == currentUserEmail) {
                        channelList.add(channel)
                    }
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
}