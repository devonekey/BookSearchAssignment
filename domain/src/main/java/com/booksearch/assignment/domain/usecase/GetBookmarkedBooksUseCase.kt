package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.Book
import com.booksearch.assignment.domain.model.query.BookmarkSort
import com.booksearch.assignment.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkedBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke(
        query: String? = null,
        sort: BookmarkSort = BookmarkSort.TITLE_ASCENDING,
        priceRange: IntRange = IntRange(0, Int.MAX_VALUE)
    ): Flow<List<Book>> = repository.getBookmarkedBooks(
        query = query,
        sort = sort,
        priceRange = priceRange
    )
}
