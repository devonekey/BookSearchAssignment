package com.booksearch.assignment.domain.repository

import com.booksearch.assignment.domain.model.Book
import com.booksearch.assignment.domain.model.query.BookmarkSort
import com.booksearch.assignment.domain.model.query.SearchSort
import com.booksearch.assignment.domain.model.query.SearchTarget
import com.booksearch.assignment.domain.util.fakeBook
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeBookRepository : BookRepository {
    private val searchBooks = fakeBook().toMutableList()
    private val bookmarkedBooks = fakeBook().toMutableList()

    override suspend fun searchBooks(
        query: String?,
        sort: SearchSort,
        page: Int,
        size: Int,
        target: SearchTarget?
    ): List<Book> {
        val filtered = if (query.isNullOrBlank()) {
            searchBooks
        } else {
            searchBooks.filter { it.title.contains(query, ignoreCase = true) }
        }
        val sorted = when (sort) {
            SearchSort.ACCURACY -> filtered.sortedBy { it.title.length }
            SearchSort.LATEST -> filtered.sortedByDescending { it.datetime }
        }

        return sorted.take(n = size)
    }

    override fun getBookmarkedBooks(
        query: String?,
        sort: BookmarkSort,
        priceRange: IntRange
    ): Flow<List<Book>> {
        val filtered = if (query.isNullOrBlank()) {
            bookmarkedBooks
        } else {
            bookmarkedBooks.filter { it.title.contains(query, ignoreCase = true) }
        }.filter { book -> book.price in priceRange }
        val sorted = when (sort) {
            BookmarkSort.TITLE_ASCENDING -> filtered.sortedBy { it.title.lowercase() }
            BookmarkSort.TITLE_DESCENDING -> filtered.sortedByDescending { it.title.lowercase() }
        }

        return flowOf(sorted)
    }

    override suspend fun addBookmark(book: Book) {
        bookmarkedBooks.add(book)
    }

    override suspend fun removeBookmark(book: Book) {
        bookmarkedBooks.remove(book)
    }

    override suspend fun isBookmark(book: Book): Boolean = bookmarkedBooks.contains(book)
}
