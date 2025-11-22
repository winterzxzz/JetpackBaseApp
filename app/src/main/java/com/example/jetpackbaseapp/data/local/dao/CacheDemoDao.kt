package com.example.jetpackbaseapp.data.local.dao

import androidx.room.*
import com.example.jetpackbaseapp.data.local.entity.CacheDemoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CacheDemoDao {

    @Query("SELECT * FROM cache_demo ORDER BY id ASC")
    fun getAllCached(): Flow<List<CacheDemoEntity>>

    @Query("SELECT * FROM cache_demo WHERE id = :id")
    suspend fun getCachedById(id: Int): CacheDemoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: CacheDemoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCache(caches: List<CacheDemoEntity>)

    @Query("DELETE FROM cache_demo")
    suspend fun clearAllCache()

    @Query("DELETE FROM cache_demo WHERE id = :id")
    suspend fun deleteCacheById(id: Int)
}
