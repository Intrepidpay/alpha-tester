package com.example.aichatassistant.di

import android.content.Context
import com.example.aichatassistant.data.AppDatabase
import com.example.aichatassistant.data.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = 
        AppDatabase.getDatabase(context)

    @Singleton
    @Provides
    fun provideProfileDao(database: AppDatabase) = 
        database.profileDao()

    @Singleton
    @Provides
    fun provideProfileRepository(dao: ProfileDao) = 
        ProfileRepository(dao)
}