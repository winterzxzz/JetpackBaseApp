package com.example.jetpackbaseapp.data.repository

import com.example.jetpackbaseapp.data.remote.api.ApiService
import com.example.jetpackbaseapp.domain.model.User
import com.example.jetpackbaseapp.domain.repository.UserRepository
import com.example.jetpackbaseapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: ApiService
) : UserRepository {

    override fun getUsers(): Flow<Resource<List<User>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getUsers()
            if (response.isSuccessful) {
                val users = response.body()?.map { dto ->
                    User(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        phone = dto.phone
                    )
                } ?: emptyList()
                emit(Resource.Success(users))
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

    override fun getUserById(id: Int): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getUserById(id)
            if (response.isSuccessful) {
                val dto = response.body()
                if (dto != null) {
                    val user = User(
                        id = dto.id,
                        name = dto.name,
                        email = dto.email,
                        phone = dto.phone
                    )
                    emit(Resource.Success(user))
                } else {
                    emit(Resource.Error("User not found"))
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
}
