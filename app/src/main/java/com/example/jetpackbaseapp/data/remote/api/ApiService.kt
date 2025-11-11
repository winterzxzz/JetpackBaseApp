package com.example.jetpackbaseapp.data.remote.api

import com.example.jetpackbaseapp.data.remote.dto.PostDto
import com.example.jetpackbaseapp.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    
    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>
    
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): Response<PostDto>
    
    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>
    
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<UserDto>
    
    @GET("posts")
    suspend fun getPostsByUser(@Query("userId") userId: Int): Response<List<PostDto>>
}
