package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.Book
import com.booksearch.assignment.domain.repository.BookRepository
import javax.inject.Inject

class ToggleBookmarkUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(
        book: Book
    ) {
        if (repository.isBookmark(book = book)) {
            repository.removeBookmark(book = book)
        } else {
            repository.addBookmark(book = book)
        }
    }
}
