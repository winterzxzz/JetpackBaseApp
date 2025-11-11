package com.example.jetpackbaseapp.data.local.dao

import androidx.room.*
import com.example.jetpackbaseapp.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    fun getPostById(postId: Int): Flow<PostEntity?>

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY timestamp DESC")
    fun getPostsByUser(userId: Int): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Delete
    suspend fun deletePost(post: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun deleteAllPosts()

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostCount(): Int
}
