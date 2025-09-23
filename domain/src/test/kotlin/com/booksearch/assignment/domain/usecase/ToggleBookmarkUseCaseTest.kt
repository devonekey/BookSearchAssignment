package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.Book
import com.booksearch.assignment.domain.model.Books
import com.booksearch.assignment.domain.model.input.GetBookmarkedBooksQuery
import com.booksearch.assignment.domain.model.input.ToggleBookmarkTarget
import com.booksearch.assignment.domain.model.output.BooksResult.Success
import com.booksearch.assignment.domain.repository.FakeBookRepositoryImpl
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ToggleBookmarkUseCaseTest {
    private lateinit var getBookmarkedBooksUseCase: GetBookmarkedBooksUseCase
    private lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase

    @BeforeEach
    fun beforeEach() {
        val repository = FakeBookRepositoryImpl()
        getBookmarkedBooksUseCase = GetBookmarkedBooksUseCase(repository = repository)
        toggleBookmarkUseCase = ToggleBookmarkUseCase(repository = repository)
    }

    @Test
    fun `도서를_북마크한다`() = runTest {
        // Given
        var output = getBookmarkedBooksUseCase(input = GetBookmarkedBooksQuery())
        var books: Books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())
        val size = books.size
        val book = Book(
            isbn = "896077183X 9788960771833",
            price = 30_000
        )

        // When
        toggleBookmarkUseCase(input = ToggleBookmarkTarget(book = book))
        output = getBookmarkedBooksUseCase(input = GetBookmarkedBooksQuery())
        books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertEquals(size + 1, books.size)
    }

    @Test
    fun `북마크된_도서로부터_북마크를_제거한다`() = runTest {
        // Given
        var output = getBookmarkedBooksUseCase(input = GetBookmarkedBooksQuery())
        var books: Books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())
        val size = books.size
        val book: Book = books.firstOrNull()
            ?: Book(isbn = "1141601591 9791141601591")

        // When
        toggleBookmarkUseCase(input = ToggleBookmarkTarget(book = book))
        output = getBookmarkedBooksUseCase(input = GetBookmarkedBooksQuery())
        books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertEquals(size - 1, books.size)
    }
}
