package com.booksearch.assignment.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class BookTest {
    @Test
    fun `도서_판매가가_별도로_지정되면_도서_판매가가_최종_판매가가_된다`() {
        // Given
        val book = Book(
            price = 30000,
            salePrice = 27000
        )

        // Expect
        assertEquals(27000, book.effectivePrice)
    }

    @Test
    fun `도서_판매가가_별도로_지정되지_않으면_도서_정가가_최종_판매가가_된다`() {
        // Given
        val book = Book(price = 30000)

        // Expect
        assertEquals(30000, book.effectivePrice)
    }

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
}
