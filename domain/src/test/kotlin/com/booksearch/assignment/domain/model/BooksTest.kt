package com.booksearch.assignment.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BooksTest {
    private lateinit var books: Books

    @BeforeAll
    fun beforeAll() {
        val data = listOf(
            Book(
                title = "Android Studio를 활용한 안드로이드 프로그래밍",
                isbn = "1156644801 9791156644804"
            ),
            Book(
                title = "안드로이드 포렌식(에이콘 디지털 포렌식 시리즈 3)",
                isbn = "8960774030 9788960774032"
            ),
            Book(
                title = "Android",
                isbn = "1430226595 9781430226598"
            )
        )
        books = Books(data = data)
    }

    @Test
    fun `특정_도서가_List를_위임받은_도서들에_포함되는지_여부를_확인할_수_있다`() {
        // Given
        val book1 = Book(isbn = "1156644801 9791156644804")
        val book2 = Book(isbn = "1787123693 9781787123694")

        // Expect
        assert(books.contains(book1))
        assert(!books.contains(book2))
    }

    @Test
    fun `List를_위임받은_도서들로부터_특정_위치의_도서를_가져올_수_있다`() {
        // Given
        val book = Book(isbn = "8960774030 9788960774032")

        // Expect
        assertEquals(book, books[1])
        assertThrows<IndexOutOfBoundsException> { books[3] }
    }

    @Test
    fun `특정_도서가_List를_위임받은_도서들로부터_몇번째에_위치하는지_확인할_수_있다`() {
        // Given
        val book1 = Book(isbn = "1430226595 9781430226598")
        val book2 = Book(isbn = "1787123693 9781787123694")

        // Expect
        assertEquals(2, books.indexOf(book1))
        assertEquals(-1, books.indexOf(book2))
    }

    @Test
    fun `List를_위임받은_도서들은_순차적으로_순회된다`() {
        // Given
        val titles = listOf(
            "Android Studio를 활용한 안드로이드 프로그래밍",
            "안드로이드 포렌식(에이콘 디지털 포렌식 시리즈 3)",
            "Android"
        )

        // When
        val iterated = books.map { book -> book.title }

        // Then
        assertIterableEquals(titles, iterated)
    }

    @Test
    fun `List를_위임받은_도서들은_현존하는_도서들만큼_크기를_반환한다`() {
        // Given
        val emptyBooks = Books(data = emptyList())

        // Expect
        assertEquals(3, books.size)
        assertEquals(0, emptyBooks.size)
    }

    @Test
    fun `List를_위임받은_두_도서들을_더하면_두_번째_항의_isEnd로_덮어쓴다`() {
        // Given
        val data = listOf(
            Book(
                title = "Android",
                isbn = "1787123693 9781787123694"
            ),
            Book(
                title = "안드로이드 프로그래밍(Android Studio를 활용한)(4판)(IT CookBook 249)",
                isbn = "1156644380 9791156644385"
            ),
            Book(
                title = "Android Forensics",
                isbn = "9781597496513"
            )
        )
        val otherBooks = Books(data = data, isEnd = true)

        // When
        val resultBooks1 = books + otherBooks
        val resultBooks2 = otherBooks + books

        // Then
        assertEquals(6, resultBooks1.size)
        assertEquals(true, resultBooks1.isEnd)
        assertEquals(false, resultBooks2.isEnd)
    }

    @Test
    fun `도서들과_어느_대상이_주어질_때_타입이_다르면_같은_도서들로_간주할_수_없다`() {
        // Given
        val otherBooks = Any()

        // Expect
        assertNotEquals(otherBooks, books)
    }

    @Test
    fun `도서들과_어느_대상이_주어질_때_타입이_같지만_크기가_다르면_같은_도서들로_간주할_수_없다`() {
        // Given
        val data = listOf(
            Book(
                title = "Android Studio를 활용한 안드로이드 프로그래밍",
                isbn = "1156644801 9791156644804"
            ),
            Book(
                title = "안드로이드 포렌식(에이콘 디지털 포렌식 시리즈 3)",
                isbn = "8960774030 9788960774032"
            ),
            Book(
                title = "Android",
                isbn = "1430226595 9781430226598"
            ),
            Book(
                title = "Android",
                isbn = "1787123693 9781787123694"
            )
        )
        val otherBooks = Books(data)

        // Expect
        assertNotEquals(otherBooks, books)
    }

    @Test
    fun `도서들과_어느_대상이_주어질_때_타입과_크기가_같지만_각_도서들의_순서가_다르면_같은_도서들로_간주할_수_없다`() {
        // Given
        val data = listOf(
            Book(
                title = "안드로이드 포렌식(에이콘 디지털 포렌식 시리즈 3)",
                isbn = "8960774030 9788960774032"
            ),
            Book(
                title = "Android",
                isbn = "1430226595 9781430226598"
            ),
            Book(
                title = "Android Studio를 활용한 안드로이드 프로그래밍",
                isbn = "1156644801 9791156644804"
            )
        )
        val otherBooks = Books(data)

        // Expect
        assertNotEquals(otherBooks, books)
    }

    @Test
    fun `도서들과_어느_대상이_주어질_때_타입과_크기가_같으며_각_도서들의_순서도_같으면_같은_도서들로_간주한다`() {
        // Given
        val data = listOf(
            Book(
                title = "Android Studio를 활용한 안드로이드 프로그래밍",
                isbn = "1156644801 9791156644804"
            ),
            Book(
                title = "안드로이드 포렌식(에이콘 디지털 포렌식 시리즈 3)",
                isbn = "8960774030 9788960774032"
            ),
            Book(
                title = "Android",
                isbn = "1430226595 9781430226598"
            )
        )
        val otherBooks = Books(data)

        // Expect
        assertEquals(otherBooks, books)
    }
}
