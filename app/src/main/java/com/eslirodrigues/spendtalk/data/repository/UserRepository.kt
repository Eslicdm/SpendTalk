package com.eslirodrigues.spendtalk.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.eslirodrigues.spendtalk.data.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserRepository {

    private val database = Firebase.database

    private val userReference = database.getReference("user")

    fun getUsers() : Flow<List<User>> = flow {
        val allUsers = mutableStateListOf<User>()
        userReference.keepSynced(true)
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.map {
                    it.getValue(User::class.java)
                }.forEach {
                    allUsers.add(it!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        emit(allUsers)
    }.flowOn(Dispatchers.IO)


    fun addUser(user: User) {
        userReference.child(user.id).setValue(User(id = user.id, email = user.email, image = user.image))
    }
}