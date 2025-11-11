package com.example.jetpackbaseapp.database.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jetpackbaseapp.database.entity.TaskEntity

interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    @Query("SELECT * FROM task WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?
    @Query("SELECT * FROM task")
    suspend fun getAllTasks(): List<TaskEntity>
    @Query("DELETE FROM task WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)
    @Query("DELETE FROM task")
    suspend fun deleteAllTasks()
}