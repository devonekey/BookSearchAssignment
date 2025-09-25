package com.booksearch.assignment.data.local

import com.booksearch.assignment.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun upsert(book: BookEntity)

    suspend fun upsertAll(books: List<BookEntity>)

    suspend fun deleteBy(isbn: String)

    fun observeBy(isbn: String): Flow<BookEntity?>

    fun observeBookmarksOrderByTitleAsc(
        minPrice: Int? = null,
        maxPrice: Int? = null
    ): Flow<List<BookEntity>>

    fun observeBookmarksOrderByTitleDesc(
        minPrice: Int? = null,
        maxPrice: Int? = null
    ): Flow<List<BookEntity>>

    fun observeBookmarkedIsbnSet(): Flow<Set<String>>
}
