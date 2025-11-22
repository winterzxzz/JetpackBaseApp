package com.example.jetpackbaseapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jetpackbaseapp.data.local.dao.CacheDemoDao
import com.example.jetpackbaseapp.data.local.entity.CacheDemoEntity

@Database(
    entities = [CacheDemoEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheDemoDao(): CacheDemoDao
}
