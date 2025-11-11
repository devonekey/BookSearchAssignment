package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.Books
import com.booksearch.assignment.domain.model.input.SearchBooksQuery
import com.booksearch.assignment.domain.model.input.SearchBooksQuery.Sort
import com.booksearch.assignment.domain.model.input.SearchBooksQuery.Target
import com.booksearch.assignment.domain.model.output.BooksResult.Success
import com.booksearch.assignment.domain.repository.FakeBookRepositoryImpl
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ResetSearchedBooksUseCaseTest {
    private lateinit var searchBooksUseCase: SearchBooksUseCase
    private lateinit var resetSearchedBooksUseCase: ResetSearchedBooksUseCase

    @BeforeEach
    fun beforeEach() {
        val repository = FakeBookRepositoryImpl()
        searchBooksUseCase = SearchBooksUseCase(repository = repository)
        resetSearchedBooksUseCase = ResetSearchedBooksUseCase(repository = repository)
    }

    @Test
    fun `검색한_도서들을_초기화하고_입력했던_조건으로_북마크된_도서들을_다시_가져온다`() = runTest {
        // Given
        val initOutput = searchBooksUseCase(
            input = SearchBooksQuery(
                keyword = "한강",
                sort = Sort.ACCURACY,
                size = 20,
                target = Target.TITLE
            )
        )
        val initBooks: Books = initOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // When
        val resetOutput = resetSearchedBooksUseCase(input = Unit)
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
