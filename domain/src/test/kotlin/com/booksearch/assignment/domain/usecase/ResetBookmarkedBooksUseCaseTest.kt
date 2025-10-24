package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.Books
import com.booksearch.assignment.domain.model.input.GetBookmarkedBooksQuery
import com.booksearch.assignment.domain.model.input.GetBookmarkedBooksQuery.Sort
import com.booksearch.assignment.domain.model.output.BooksResult.Success
import com.booksearch.assignment.domain.repository.FakeBookRepositoryImpl
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ResetBookmarkedBooksUseCaseTest {
    private lateinit var getBookmarkedBooksUseCase: GetBookmarkedBooksUseCase
    private lateinit var resetBookmarkedBooksUseCase: ResetBookmarkedBooksUseCase

    @BeforeEach
    fun beforeEach() {
        val repository = FakeBookRepositoryImpl()
        getBookmarkedBooksUseCase = GetBookmarkedBooksUseCase(repository = repository)
        resetBookmarkedBooksUseCase = ResetBookmarkedBooksUseCase(repository = repository)
    }

    @Test
    fun `가져온_북마크된_도서들을_초기화하고_입력했던_조건으로_북마크된_도서들을_다시_가져온다`() = runTest {
        // Given
        val initOutput = getBookmarkedBooksUseCase(
            input = GetBookmarkedBooksQuery(
                keyword = "한강",
                sort = Sort.TITLE_DESCENDING,
                priceRange = IntRange(10_000, 30_000)
            )
        )
        val initBooks: Books = initOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // When
        val resetOutput = resetBookmarkedBooksUseCase(input = Unit)
        val resetBooks: Books = resetOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertArrayEquals(arrayOf(initBooks), arrayOf(resetBooks))
    }
}
