package com.booksearch.assignment.domain.usecase

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SearchBooksUseCaseTest {
    @Test
    fun `정확도순으로_특정_검색어에_해당하는_도서들을_가져온다`() {
        // Given
        val query = "Android"

        // When
        val result = false

        // Then
        assertTrue(result)
    }

    @Test
    fun `발간일순으로_특정_검색어에_해당하는_도서들을_가져온다`() {
        // Given
        val query = "Android"

        // When
        val result = false

        // Then
        assertTrue(result)
    }
}
