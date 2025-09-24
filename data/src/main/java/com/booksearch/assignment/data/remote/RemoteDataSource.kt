package com.booksearch.assignment.data.remote

import com.booksearch.assignment.data.remote.dto.BookResponseDto

interface RemoteDataSource {
    suspend fun searchBooks(
        query: String,
        sort: String? = null,
        page: Int? = null,
        size: Int? = null,
        target: String? = null
    ): BookResponseDto
}
