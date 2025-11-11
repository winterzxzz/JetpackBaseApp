package com.example.jetpackbaseapp.domain.repository

import com.example.jetpackbaseapp.domain.model.Post
import com.example.jetpackbaseapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPosts(): Flow<Resource<List<Post>>>
    fun getPostById(id: Int): Flow<Resource<Post>>
    fun getPostsByUser(userId: Int): Flow<Resource<List<Post>>>
}
