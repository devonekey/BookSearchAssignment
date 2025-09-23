package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.Book
import com.booksearch.assignment.domain.model.query.SearchSort
import com.booksearch.assignment.domain.model.query.SearchTarget
import com.booksearch.assignment.domain.repository.BookRepository
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(
        query: String? = null,
        sort: SearchSort = SearchSort.ACCURACY,
        target: SearchTarget? = null
    ): List<Book> = repository.searchBooks(
        query = query,
        sort = sort,
        target = target
    )
}
