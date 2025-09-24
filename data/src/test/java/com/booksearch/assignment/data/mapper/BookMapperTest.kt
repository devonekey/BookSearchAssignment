package com.booksearch.assignment.data.mapper

import com.booksearch.assignment.data.local.entity.BookEntity
import com.booksearch.assignment.data.remote.dto.BookDto
import com.booksearch.assignment.data.util.fakeBook
import com.booksearch.assignment.data.util.fakeBookDto
import com.booksearch.assignment.data.util.fakeBookEntity
import com.booksearch.assignment.domain.model.Book
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BookMappersTest {
    private lateinit var fakeBook: Book
    private lateinit var fakeBookDto: BookDto
    private lateinit var fakeBookEntity: BookEntity

    @BeforeEach
    fun setUp() {
        fakeBook = fakeBook()
        fakeBookDto = fakeBookDto()
        fakeBookEntity = fakeBookEntity()
    }

    @Test
    fun `Book은_BookEntity로_변환될_수_있다`() {
        // Given
        val entity = fakeBook.toEntity()

        // Expect
        assertEquals(fakeBook.isbn, entity.isbn)
        assertEquals(fakeBook.title, entity.title)
        assertEquals(fakeBook.contents, entity.contents)
        assertEquals(fakeBook.url, entity.url)
        assertEquals(fakeBook.datetime, entity.datetime)
        assertEquals(fakeBook.authorList, entity.authors)
        assertEquals(fakeBook.publisher, entity.publisher)
        assertEquals(fakeBook.translatorList, entity.translators)
        assertEquals(fakeBook.price, entity.price)
        assertEquals(fakeBook.salePrice, entity.salePrice)
        assertEquals(fakeBook.thumbnail, entity.thumbnail)
        assertEquals(fakeBook.status, entity.status)
        assertEquals(31500, entity.effectivePrice)
    }

    @Test
    fun `BookDto는_Book으로_변환될_수_있다`() {
        // Given
        val domain = fakeBookDto.toDomain()

        // Expect
        assertEquals(fakeBookDto.title, domain.title)
        assertEquals(fakeBookDto.contents, domain.contents)
        assertEquals(fakeBookDto.url, domain.url)
        assertEquals(fakeBookDto.isbn, domain.isbn)
        assertEquals(fakeBookDto.datetime, domain.datetime)
        assertEquals(fakeBookDto.authors, domain.authorList)
        assertEquals(fakeBookDto.publisher, domain.publisher)
        assertEquals(fakeBookDto.translators, domain.translatorList)
        assertEquals(fakeBookDto.price, domain.price)
        assertEquals(fakeBookDto.salePrice, domain.salePrice)
        assertEquals(fakeBookDto.thumbnail, domain.thumbnail)
        assertEquals(fakeBookDto.status, domain.status)
        assertFalse(domain.isBookmarked)
    }

    @Test
    fun `BookDto는_BookEntity로_변환될_수_있다`() {
        // Given
        val entity = fakeBookDto.toEntity()

        // Expect
        assertEquals(fakeBookDto.isbn, entity.isbn)
        assertEquals(fakeBookDto.title, entity.title)
        assertEquals(fakeBookDto.contents, entity.contents)
        assertEquals(fakeBookDto.url, entity.url)
        assertEquals(fakeBookDto.datetime, entity.datetime)
        assertEquals(fakeBookDto.authors, entity.authors)
        assertEquals(fakeBookDto.publisher, entity.publisher)
        assertEquals(fakeBookDto.translators, entity.translators)
        assertEquals(fakeBookDto.price, entity.price)
        assertEquals(fakeBookDto.salePrice, entity.salePrice)
        assertEquals(fakeBookDto.thumbnail, entity.thumbnail)
        assertEquals(fakeBookDto.status, entity.status)
        assertEquals(31500, entity.effectivePrice)
    }

    // 3) Entity → Domain(Book)
    @Test
    fun `BookEntity는_Book으로_변환될_수_있다`() {
        // Given
        val domain = fakeBookEntity.toDomain()

        // Expect
        assertEquals(fakeBookEntity.title, domain.title)
        assertEquals(fakeBookEntity.contents, domain.contents)
        assertEquals(fakeBookEntity.url, domain.url)
        assertEquals(fakeBookEntity.isbn, domain.isbn)
        assertEquals(fakeBookEntity.datetime, domain.datetime)
        assertEquals(fakeBookEntity.authors, domain.authorList)
        assertEquals(fakeBookEntity.publisher, domain.publisher)
        assertEquals(fakeBookEntity.translators, domain.translatorList)
        assertEquals(fakeBookEntity.price, domain.price)
        assertEquals(fakeBookEntity.salePrice, domain.salePrice)
        assertEquals(fakeBookEntity.thumbnail, domain.thumbnail)
        assertEquals(fakeBookEntity.status, domain.status)
        assertTrue(domain.isBookmarked)
    }

    @Test
    fun `BookDto는_Book으로_변환하기_전에_북마크된_도서라면_변활할_때_북마크됐음을_표시할_수_있다`() {
        // Given
        val domain = fakeBookDto.toDomain(isBookmarked = true)

        // Expect
        assertTrue(domain.isBookmarked)
    }

    @Test
    fun `도서_정가와_도서_판매가_중에_0을_초과하는_최저_금액이_최종_판매가가_된다`() {
        assertEquals(32000, effectivePrice(40000, 32000))
        assertEquals(40000, effectivePrice(40000, 0))
        assertEquals(40000, effectivePrice(40000, -1))
    }
}
