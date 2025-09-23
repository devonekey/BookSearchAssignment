package com.booksearch.assignment.domain.usecase

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GetBookmarkedBooksUseCaseTest {
    @Test
    fun `북마크된_도서들을_도서_제목_오름차순으로_가져온다`() {
        // Given
        val query = "Android"

        // When
        val result = false

        // Then
        assertTrue(result)
    }

    @Test
    fun `북마크된_도서들을_도서_제목_내림차순으로_가져온다`() {
        // Given
        val query = "Android"

        // When
        val result = false

        // Then
        assertTrue(result)
    }

    @Test
    fun `도서_금액에_맞는_도서들을_가져온다`() {
        // Given
        val query = "Android"
        val priceRange = IntRange(10_000, 30_000)

        // When
        val result = false

        // Then
        assertTrue(result)
    }
}
