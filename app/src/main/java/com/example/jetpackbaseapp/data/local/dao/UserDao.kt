package com.example.jetpackbaseapp.data.local.dao

import androidx.room.*
import com.example.jetpackbaseapp.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Int): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
