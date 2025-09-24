package com.booksearch.assignment.data.di

import android.content.Context
import androidx.room.Room
import com.booksearch.assignment.data.local.AppDatabase
import com.booksearch.assignment.data.local.dao.BookDao
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
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        @DatabaseName databaseName: String,
        @InMemory InMemory: Boolean,
    ): AppDatabase =
        if (InMemory) {
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        } else {
            Room.databaseBuilder(context, AppDatabase::class.java, databaseName)
                .fallbackToDestructiveMigration()
                .build()
        }

    @Provides
    fun provideBookDao(database: AppDatabase): BookDao = database.bookDao()
}
