package com.eslirodrigues.spendtalk.data.repository

import com.eslirodrigues.spendtalk.data.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserRepository {

    private val database = Firebase.database

    private val userReference = database.getReference("user")

    fun addUser(user: User) {
        userReference.child(user.id).setValue(User(id = user.id, email = user.email, image = user.image))
    }
}