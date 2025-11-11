package com.example.jetpackbaseapp.di

import com.example.jetpackbaseapp.data.remote.api.ApiService
import com.example.jetpackbaseapp.data.repository.PostRepositoryImpl
import com.example.jetpackbaseapp.data.repository.UserRepositoryImpl
import com.example.jetpackbaseapp.domain.repository.PostRepository
import com.example.jetpackbaseapp.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePostRepository(api: ApiService): PostRepository {
        return PostRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: ApiService): UserRepository {
        return UserRepositoryImpl(api)
    }
}
