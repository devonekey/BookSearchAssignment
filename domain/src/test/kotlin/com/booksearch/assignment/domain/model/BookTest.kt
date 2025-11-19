package com.booksearch.assignment.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class BookTest {
    @Test
    fun `국제_표준_도서번호가_같으면_같은_도서로_간주한다`() {
        // Given
        val book = Book(
            title = "THE ANDROID DEVLOPER S COOKBOOK(한국어판)",
            isbn = "896077183X 9788960771833"
        )
        val otherBook = Book(
            title = "THE ANDROID DEVLOPER S COOKBOOK(한국어ㅍ나)",
            isbn = "896077183X 9788960771833"
        )

        // Expect
        assertEquals(book, otherBook)
    }

    @Test
    fun `국제_표준_도서번호가_다르면_다른_도서로_간주한다`() {
        // Given
        val book = Book(
            title = "THE ANDROID DEVLOPER S COOKBOOK(한국어판)",
            isbn = "896077183X 9788960771833"
        )
        val otherBook = Book(
            title = "THE ANDROID DEVLOPER S COOKBOOK(한국어판)",
            isbn = "896077183X"
        )

        // Expect
        assertNotEquals(book, otherBook)
    }

    @Test
    fun `도서_판매가가_별도로_지정되지_않으면_최종_판매가는_도서_정가가_된다`() {
        // Given
        val price = 30000

        // When
        val book = Book(price = price)

        // Then
        assertEquals(price, book.effectivePrice)
    }

    @Test
    fun `도서_판매가가_양수이면_최종_판매가는_도서_판매가가_된다`() {
        // Given
        val price = 30000
        val salePrice = 27000

        // When
        val book = Book(price = price, salePrice = salePrice)

        // Then
        assertEquals(salePrice, book.effectivePrice)
    }

    @Test
    fun `도서_판매가가_0이면_최종_판매가는_도서_정가가_된다`() {
        // Given
        val price = 30000
        val salePrice = 0

        // When
        val book = Book(price = price, salePrice = salePrice)

        // Then
        assertEquals(price, book.effectivePrice)
    }

    @Test
    fun `도서_판매가가_음수면_최종_판매가는_도서_정가가_된다`() {
        // Given
        val price = 30000
        val salePrice = -1

        // When
        val book = Book(price = price, salePrice = salePrice)

        // Then
        assertEquals(price, book.effectivePrice)
    }

    @Test
    fun `도서_정가와_도서_판매가가_같으면_최종_판매가는_그_금액이_된다`() {
        // Given
        val price = 30000
        val salePrice = 30000

        // When
        val book = Book(price = price, salePrice = salePrice)

        // Then
        assertEquals(price, book.effectivePrice)
    }

    @Test
    fun `도서_정가보다_도서_판매가가_높아도_최종_판매가는_도서_판매가가_된다`() {
        // Given
        val price = 30000
        val salePrice = 33000

        // When
        val book = Book(price = price, salePrice = salePrice)

        // Then
        assertEquals(salePrice, book.effectivePrice)
    }
}
