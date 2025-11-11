package com.example.jetpackbaseapp.data.repository

import com.example.jetpackbaseapp.data.remote.api.ApiService
import com.example.jetpackbaseapp.domain.model.Post
import com.example.jetpackbaseapp.domain.repository.PostRepository
import com.example.jetpackbaseapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val api: ApiService
) : PostRepository {

    override fun getPosts(): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getPosts()
            if (response.isSuccessful) {
                val posts = response.body()?.map { dto ->
                    Post(
                        id = dto.id,
                        userId = dto.userId,
                        title = dto.title,
                        body = dto.body
                    )
                } ?: emptyList()
                emit(Resource.Success(posts))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Connection error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error: ${e.localizedMessage}"))
        }
    }

    override fun getPostById(id: Int): Flow<Resource<Post>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getPostById(id)
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val post = Post(
                        id = dto.id,
                        userId = dto.userId,
                        title = dto.title,
                        body = dto.body
                    )
                    emit(Resource.Success(post))
                } else {
                    emit(Resource.Error("Post not found"))
                }
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Connection error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error: ${e.localizedMessage}"))
        }
    }

    override fun getPostsByUser(userId: Int): Flow<Resource<List<Post>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getPostsByUser(userId)
            if (response.isSuccessful) {
                val posts = response.body()?.map { dto ->
                    Post(
                        id = dto.id,
                        userId = dto.userId,
                        title = dto.title,
                        body = dto.body
                    )
                } ?: emptyList()
                emit(Resource.Success(posts))
            } else {
                emit(Resource.Error("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        } catch (e: IOException) {
            emit(Resource.Error("Connection error: ${e.localizedMessage}"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error: ${e.localizedMessage}"))
        }
    }
}
