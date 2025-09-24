package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.query.BookmarkSort
import com.booksearch.assignment.domain.repository.BookRepository
import com.booksearch.assignment.domain.repository.FakeBookRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetBookmarkedBooksUseCaseTest {
    private lateinit var repository: BookRepository
    private lateinit var useCase: GetBookmarkedBooksUseCase

    @BeforeEach
    fun setup() {
        repository = FakeBookRepository()
        useCase = GetBookmarkedBooksUseCase(repository = repository)
    }

    @Test
    fun `북마크된_도서들을_도서_제목_오름차순으로_가져온다`() = runTest {
        // Given
        val query = "Android"

        // When
        val result = useCase.invoke(
            query = query,
            sort = BookmarkSort.TITLE_ASCENDING
        )

        // Then
        assertEquals("Android", result.first().first().title)
    }

    @Test
    fun `북마크된_도서들을_도서_제목_내림차순으로_가져온다`() = runTest {
        // Given
        val query = "Android"

        // When
        val result = useCase.invoke(
            query = query,
            sort = BookmarkSort.TITLE_DESCENDING
        )

        // Then
        assertEquals(
            "안드로이드 프로그래밍(Android Studio를 활용한)(4판)(IT CookBook 249)",
            result.first().first().title
        )
    }

    @Test
    fun `도서_금액에_맞는_도서들을_가져온다`() = runTest {
        // Given
        val query = "Android"
        val priceRange = IntRange(10_000, 30_000)

        // When
        val result = useCase.invoke(
            query = query,
            priceRange = priceRange
        )

        // Then
        assertEquals(3, result.first().size)
    }
}
