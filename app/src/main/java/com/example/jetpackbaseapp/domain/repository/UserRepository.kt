package com.example.jetpackbaseapp.domain.repository

import com.example.jetpackbaseapp.domain.model.User
import com.example.jetpackbaseapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers(): Flow<Resource<List<User>>>
    fun getUserById(id: Int): Flow<Resource<User>>
}
