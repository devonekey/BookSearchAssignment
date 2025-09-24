package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.Book
import com.booksearch.assignment.domain.repository.BookRepository
import com.booksearch.assignment.domain.repository.FakeBookRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.Locale

class ToggleBookmarkUseCaseTest {
    private lateinit var repository: BookRepository
    private lateinit var getBookmarkedBooksUseCase: GetBookmarkedBooksUseCase
    private lateinit var toggleBookmarkUseCase: ToggleBookmarkUseCase

    @BeforeEach
    fun setup() {
        repository = FakeBookRepository()
        getBookmarkedBooksUseCase = GetBookmarkedBooksUseCase(repository = repository)
        toggleBookmarkUseCase = ToggleBookmarkUseCase(repository = repository)
    }

    @Test
    fun `도서를_북마크한다`() = runTest {
        // Given
        var getResult = getBookmarkedBooksUseCase.invoke()
        val book = Book(
            title = "THE ANDROID DEVLOPER S COOKBOOK(한국어판)",
            contents = "『The Android Developer's Cookbook 한국어판』은 초보 개발자가 즉시 안드로이드 애플리케이션을 개발할 수 있는 능력을 갖출 수 있도록 도와 주는 책이다...",
            url = "https://search.daum.net/search?w=bookpage&bookId=4922747&q=Rxjava+for+Android+Developers",
            isbn = "896077183X 9788960771833",
            datetime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).parse("2011-02-23T00:00:00.000+09:00"),
            authorList = listOf("제임스 스틸", "넬슨 토"),
            publisher = "에이콘출판",
            translatorList = listOf("장재현"),
            price = 30000,
            salePrice = 27000,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F837792%3Ftimestamp%3D20220902173044",
            status = "정상판매",
            isBookmarked = true
        )
        assertEquals(19, getResult.first().size)

        // When
        toggleBookmarkUseCase.invoke(book)
        getResult = getBookmarkedBooksUseCase.invoke()

        // Then
        assertEquals(20, getResult.first().size)
    }

    @Test
    fun `북마크된_도서로부터_북마크를_제거한다`() = runTest {
        // Given
        var getResult = getBookmarkedBooksUseCase.invoke()
        val book = getResult.first().first()
        assertEquals(19, getResult.first().size)

        // When
        toggleBookmarkUseCase.invoke(book)
        getResult = getBookmarkedBooksUseCase.invoke()

        // Then
        assertEquals(18, getResult.first().size)
    }
}
