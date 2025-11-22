package com.example.jetpackbaseapp.di

import android.content.Context
import androidx.room.Room
import com.example.jetpackbaseapp.data.local.dao.CacheDemoDao
import com.example.jetpackbaseapp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "jetpack_base_app_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCacheDemoDao(database: AppDatabase): CacheDemoDao {
        return database.cacheDemoDao()
    }
}
