package com.example.jetpackbaseapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity để demo caching với Room Database
 */
@Entity(tableName = "cache_demo")
data class CacheDemoEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val data: String,
    val cachedAt: Long = System.currentTimeMillis()
)
