package com.booksearch.assignment.domain.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EffectivePriceTest {
    @Test
    fun `도서_판매가가_양수이면_도서_판매가를_반환한다`() {
        // Given
        val price = 35000
        val salePrice = 31500

        // When
        val effectivePrice = calculateEffectivePrice(price = price, salePrice = salePrice)

        // Then
        assertEquals(salePrice, effectivePrice)
    }

    @Test
    fun `도서_판매가가_0이면_도서_정가를_반환한다`() {
        // Given
        val price = 35000
        val salePrice = 0

        // When
        val effectivePrice = calculateEffectivePrice(price = price, salePrice = salePrice)

        // Then
        assertEquals(price, effectivePrice)
    }

    @Test
    fun `도서_판매가가_음수이면_도서_정가를_반환한다`() {
        // Given
        val price = 35000
        val salePrice = -1

        // When
        val effectivePrice = calculateEffectivePrice(price = price, salePrice = salePrice)

        // Then
        assertEquals(price, effectivePrice)
    }

    @Test
    fun `도서_정가와_도서_판매가가_같으면_그_금액을_반환한다`() {
        // Given
        val price = 35000
        val salePrice = 35000

        // When
        val effectivePrice = calculateEffectivePrice(price = price, salePrice = salePrice)

        // Then
        assertEquals(price, effectivePrice)
    }

    @Test
    fun `도서_정가보다_도서_판매가가_높아도_도서_판매가를_반환한다`() {
        // Given
        val price = 35000
        val salePrice = 38500

        // When
        val effectivePrice = calculateEffectivePrice(price = price, salePrice = salePrice)

        // Then
        assertEquals(salePrice, effectivePrice)
    }
}
