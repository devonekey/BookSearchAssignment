package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.Books
import com.booksearch.assignment.domain.model.input.SearchBooksQuery
import com.booksearch.assignment.domain.model.input.SearchBooksQuery.Sort
import com.booksearch.assignment.domain.model.input.SearchBooksQuery.Target
import com.booksearch.assignment.domain.model.output.BooksResult
import com.booksearch.assignment.domain.model.output.BooksResult.Failure.Code
import com.booksearch.assignment.domain.model.output.BooksResult.Success
import com.booksearch.assignment.domain.repository.FakeBookRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SearchBooksUseCaseTest {
    private lateinit var useCase: SearchBooksUseCase

    @BeforeEach
    fun beforeEach() {
        val repository = FakeBookRepositoryImpl()
        useCase = SearchBooksUseCase(repository = repository)
    }

    @Test
    fun `검색어에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val input = SearchBooksQuery(keyword = "한강")

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
            "한강 스페셜 에디션",
            books.first().title
        )
        assertEquals(
            "흰",
            books.last().title
        )
    }

    @Test
    fun `검색어가_없으면_도서들을_가져올_수_없다`() = runTest {
        // Given
        val input = SearchBooksQuery()

        // When
        val output: Flow<BooksResult> = useCase(input = input)

        // Then
        assertEquals(
            BooksResult.Failure(code = Code.NOT_FOUND),
            output.drop(1).first()
        )
    }

    @Test
    fun `정확도순으로_검색어에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val input = SearchBooksQuery(
            keyword = "한강",
            sort = Sort.ACCURACY
        )

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
            "한강 스페셜 에디션",
            books.first().title
        )
        assertEquals(
            "흰",
            books.last().title
        )
    }

    @Test
    fun `발간일순으로_검색어에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val input = SearchBooksQuery(
            keyword = "한강",
            sort = Sort.LATEST
        )

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
            "한강",
            books.first().title
        )
        assertEquals(
            "채식주의자",
            books.last().title
        )
        assertTrue(
            books.zipWithNext()
                .all { (prev, next) -> prev.datetime.time >= next.datetime.time }
        )
    }

    @Test
    fun `결과_정렬_방식을_적용하지_않고_가져온_도서들은_정확도순으로_가져온_도서들과_모두_같다`() = runTest {
        // Given
        val input = SearchBooksQuery(keyword = "한강")
        val inputWithSort = SearchBooksQuery(
            keyword = "한강",
            sort = Sort.ACCURACY,
            page = 1
        )

        // When
        val output: Flow<BooksResult> = useCase(input = input)
        val books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())
        val sortedOutput: Flow<BooksResult> = useCase(input = inputWithSort)
        val sortedBooks = sortedOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertArrayEquals(arrayOf(sortedBooks), arrayOf(books))
    }

    @Test
    fun `페이지_번호를_지정하여_검색어에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val inputWithFirstPage = SearchBooksQuery(
            keyword = "한강",
            sort = Sort.LATEST,
            page = 1
        )
        val inputWithSecondPage = SearchBooksQuery(
            keyword = "한강",
            sort = Sort.LATEST,
            page = 2
        )

        // When
        val firstOutput: Flow<BooksResult> = useCase(input = inputWithFirstPage)
        val firstBooks = firstOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())
        val secondOutput: Flow<BooksResult> = useCase(input = inputWithSecondPage)
        val secondBooks = secondOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertTrue(
            firstBooks.minOf { it.datetime.time } >= secondBooks.maxOf { it.datetime.time }
        )
    }

    @Test
    fun `페이지_번호를_지정하지_않고_같은_입력으로_도서들을_가져오면_다음_페이지_번호에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val input = SearchBooksQuery(
            keyword = "한강",
            sort = Sort.LATEST
        )

        // When
        val firstOutput: Flow<BooksResult> = useCase(input = input)
        val firstBooks = firstOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())
        val secondOutput: Flow<BooksResult> = useCase(input = input)
        val secondBooks = secondOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertTrue(firstBooks.minOf { it.datetime.time } >= secondBooks.maxOf { it.datetime.time })
    }

    @Test
    fun `한_페이지에_보여질_문서_수를_지정하여_검색어에_해당하는_도서들을_지정한만큼_가져온다`() = runTest {
        // Given
        val input = SearchBooksQuery(
            keyword = "한강",
            size = 20
        )

        // When
        val output: Flow<BooksResult> = useCase(input = input)
        val books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertEquals(20, books.size)
    }

    @Test
    fun `한_페이지에_보여질_문서_수를_지정하지_않아도_검색어에_해당하는_도서들을_10개만큼_가져온다`() = runTest {
        // Given
        val input = SearchBooksQuery(keyword = "한강")

        // When
        val output: Flow<BooksResult> = useCase(input = input)
        val books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertEquals(10, books.size)
    }

    @Test
    fun `도서들을_반복해서_가져오면_마지막_페이지에_도달한다`() = runTest {
        // Given
        val input = SearchBooksQuery(
            keyword = "한강",
            size = 30
        )

        // When
        val firstOutput: Flow<BooksResult> = useCase(input = input)
        val firstBooks = firstOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())
        val secondOutput: Flow<BooksResult> = useCase(input = input)
        val secondBooks = secondOutput.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertFalse(firstBooks.isEnd)
        assertTrue(secondBooks.isEnd)
    }

    @Test
    fun `검색어_적용_범위를_제목으로_적용하여_검색어에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val input = SearchBooksQuery(
            keyword = "한강",
            target = Target.TITLE
        )

        // When
        val output: Flow<BooksResult> = useCase(input = input)
        val books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertTrue(books.isNotEmpty())
    }

    @Test
    fun `검색어_적용_범위를_ISBN으로_적용하여_검색어에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val input = SearchBooksQuery(
            keyword = "1141601591 9791141601591",
            target = Target.ISBN
        )

        // When
        val output: Flow<BooksResult> = useCase(input = input)
        val books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertEquals("한강 스페셜 에디션", books.first().title)
    }

    @Test
    fun `검색어_적용_범위를_출판사로_적용하여_검색어에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val input = SearchBooksQuery(
            keyword = "문학동네",
            target = Target.PUBLISHER
        )

        // When
        val output: Flow<BooksResult> = useCase(input = input)
        val books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertTrue(books.all { "문학동네" == it.publisher })
    }

    @Test
    fun `검색어_적용_범위를_인명으로_적용하여_검색어에_해당하는_도서들을_가져온다`() = runTest {
        // Given
        val input = SearchBooksQuery(
            keyword = "한강",
            target = Target.PERSON
        )

        // When
        val output: Flow<BooksResult> = useCase(input = input)
        val books = output.drop(1)
            .first()
            .takeIf { it is Success }
            ?.let { it as Success }
            ?.books
            ?: Books(data = emptyList())

        // Then
        assertTrue(books.all { "한강" in it.authors })
    }
}
