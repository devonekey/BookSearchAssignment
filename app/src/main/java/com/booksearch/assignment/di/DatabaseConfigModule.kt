package com.booksearch.assignment.di

import com.booksearch.assignment.data.di.DatabaseName
import com.booksearch.assignment.data.di.InMemory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseConfigModule {
    @Provides
    @Singleton
    @DatabaseName
    fun provideDatabaseName(): String = "BookDatabase"

    @Provides
    @Singleton
    @InMemory
    fun provideInMemory(): Boolean = false
}
