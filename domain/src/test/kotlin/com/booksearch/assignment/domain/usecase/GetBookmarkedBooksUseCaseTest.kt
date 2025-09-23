package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.Books
import com.booksearch.assignment.domain.model.input.GetBookmarkedBooksQuery
import com.booksearch.assignment.domain.model.input.GetBookmarkedBooksQuery.Sort
import com.booksearch.assignment.domain.model.output.BooksResult
import com.booksearch.assignment.domain.model.output.BooksResult.Success
import com.booksearch.assignment.domain.repository.FakeBookRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.collections.zipWithNext

class GetBookmarkedBooksUseCaseTest {
    private lateinit var useCase: GetBookmarkedBooksUseCase

    @BeforeEach
    fun beforeEach() {
        val repository = FakeBookRepositoryImpl()
        useCase = GetBookmarkedBooksUseCase(repository = repository)
    }

    @Test
    fun `검색어에_해당하는_북마크된_도서들을_가져온다`() = runTest {
        // Given
        val input = GetBookmarkedBooksQuery(keyword = "한강")

        // When
        val output: Flow<BooksResult> = useCase(input = input)
        val books: Books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertEquals(
            "검은 사슴",
            books.first().title
        )
        assertEquals(
            "흰",
            books.last().title
        )
    }

    @Test
    fun `검색어가_없어도_북마크된_도서들을_가져올_수_있다`() = runTest {
        // Given
        val input = GetBookmarkedBooksQuery()

        // When
        val output: Flow<BooksResult> = useCase(input = input)
        val books: Books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertEquals(
            "검은 사슴",
            books.first().title
        )
        assertEquals(
            "흰",
            books.last().title
        )
    }

    @Test
    fun `도서_제목_오름차순으로_북마크된_도서들을_가져온다`() = runTest {
        // Given
        val input = GetBookmarkedBooksQuery(sort = Sort.TITLE_ASCENDING)

        // When
        val output = useCase(input = input)
        val books: Books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertTrue(
            books.zipWithNext()
                .all { (prev, next) -> prev.title <= next.title }
        )
    }

    @Test
    fun `도서_제목_내림차순으로_북마크된_도서들을_가져온다`() = runTest {
        // Given
        val input = GetBookmarkedBooksQuery(sort = Sort.TITLE_DESCENDING)

        // When
        val output = useCase(input = input)
        val books: Books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertTrue(
            books.zipWithNext()
                .all { (prev, next) -> prev.title >= next.title }
        )
    }

    @Test
    fun `결과_정렬_방식을_적용하지_않고_가져온_북마크된_도서들은_제목_오름차순으로_가져온_북마크된_도서들과_모두_같다`() = runTest {
        // Given
        val input = GetBookmarkedBooksQuery()
        val inputWithSort = GetBookmarkedBooksQuery(sort = Sort.TITLE_ASCENDING)

        // When
        val output = useCase(input = input)
        val books: Books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())
        val sortedOutput = useCase(input = inputWithSort)
        val sortedBooks: Books = sortedOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertArrayEquals(arrayOf(sortedBooks), arrayOf(books))
    }

    @Test
    fun `도서_금액에_맞는_북마크된_도서들을_가져온다`() = runTest {
        // Given
        val input = GetBookmarkedBooksQuery(priceRange = IntRange(10_000, 30_000))

        // When
        val output = useCase(input = input)
        val books: Books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertTrue(books.all { it.effectivePrice in IntRange(10_000, 30_000) })
    }
}
