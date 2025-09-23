package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.query.SearchSort
import com.booksearch.assignment.domain.repository.BookRepository
import com.booksearch.assignment.domain.repository.FakeBookRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchBooksUseCaseTest {
    private lateinit var repository: BookRepository
    private lateinit var useCase: SearchBooksUseCase

    @BeforeEach
    fun setup() {
        repository = FakeBookRepository()
        useCase = SearchBooksUseCase(repository = repository)
    }

    @Test
    fun `정확도순으로_특정_검색어에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val query = "Android"

        // When
        val result = useCase(
            query = query,
            sort = SearchSort.ACCURACY
        )

        // Then
        assertEquals("Android", result.first().title)
        assertEquals("IT로켓009 구글 이스터에그 Ⅸ. 안드로이드(Android) & 크롬캐스트(Chromecast)", result.last().title)
    }

    @Test
    fun `발간일순으로_특정_검색어에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val query = "Android"

        // When
        val result = useCase(
            query = query,
            sort = SearchSort.LATEST
        )

        // Then
        assertEquals("Android", result.first().title)
        assertEquals("Android (Operating System)", result.last().title)
    }
}
