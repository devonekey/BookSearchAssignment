package com.booksearch.assignment.data.repository

import com.booksearch.assignment.data.local.LocalDataSource
import com.booksearch.assignment.data.mapper.toDomain
import com.booksearch.assignment.data.mapper.toEntity
import com.booksearch.assignment.data.remote.RemoteDataSource
import com.booksearch.assignment.domain.model.Book
import com.booksearch.assignment.domain.model.query.BookmarkSort
import com.booksearch.assignment.domain.model.query.SearchSort
import com.booksearch.assignment.domain.model.query.SearchTarget
import com.booksearch.assignment.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : BookRepository {
    private data class QueryKey(
        val query: String,
        val sort: SearchSort,
        val target: SearchTarget?
    )

    private val cache = mutableListOf<Book>()
    private val cacheLock = Mutex()
    private var currentPage = 1
    private var lastKey: QueryKey? = null

    override suspend fun searchBooks(
        query: String?,
        sort: SearchSort,
        page: Int,
        size: Int,
        target: SearchTarget?
    ): List<Book> {
        val query = query?.trim()
            .orEmpty()

        if (query.isBlank()) {
            return emptyList()
        }

        val queryKey = QueryKey(query, sort, target)

        cacheLock.withLock {
            if (lastKey != queryKey) {
                cache.clear()

                currentPage = 1
                lastKey = queryKey
            }
        }

        val bookResponseDto = remoteDataSource.searchBooks(
            query = query,
            sort = sort.toParam(),
            page = currentPage,
            size = size,
            target = target?.toParam()
        )
        val bookmarkedIsbnSet = localDataSource.observeBookmarkedIsbnSet().first()
        val bookList = bookResponseDto.documents
            .map { bookDto -> bookDto.toDomain(bookDto.isbn in bookmarkedIsbnSet) }

        return cacheLock.withLock {
            cache += bookList
            currentPage++

            cache.toList()
        }
    }

    override fun getBookmarkedBooks(
        query: String?,
        sort: BookmarkSort,
        priceRange: IntRange
    ): Flow<List<Book>> {
        val minPrice = priceRange.first
        val maxPrice = priceRange.last
        val base = when (sort) {
            BookmarkSort.TITLE_ASCENDING -> localDataSource.observeBookmarksOrderByTitleAsc(
                minPrice = minPrice,
                maxPrice = maxPrice
            )
            BookmarkSort.TITLE_DESCENDING -> localDataSource.observeBookmarksOrderByTitleDesc(
                minPrice = minPrice,
                maxPrice = maxPrice
            )
        }
        val trimmedQuery = query?.trim().orEmpty()

        if (trimmedQuery.isBlank()) {
            return base.map { list -> list.map { it.toDomain() } }
        }

        return base.map { list ->
            val filteredBookList = list.filter { e ->
                e.title.contains(trimmedQuery, ignoreCase = true) ||
                        e.publisher.contains(trimmedQuery, ignoreCase = true) ||
                        e.isbn.contains(trimmedQuery, ignoreCase = true) ||
                        e.authors.any { it.contains(trimmedQuery, ignoreCase = true) }
            }

            filteredBookList.map { it.toDomain() }
        }
    }

    override suspend fun addBookmark(book: Book) {
        localDataSource.upsert(book.toEntity())

        cacheLock.withLock {
            val index = cache.indexOfFirst { it.isbn == book.isbn }

            if (index >= 0) {
                cache[index] = cache[index].copy(isBookmarked = true)
            }
        }
    }

    override suspend fun removeBookmark(book: Book) {
        localDataSource.deleteBy(book.isbn)

        cacheLock.withLock {
            val index = cache.indexOfFirst { it.isbn == book.isbn }

            if (index >= 0) {
                cache[index] = cache[index].copy(isBookmarked = false)
            }
        }
    }

    override suspend fun isBookmark(book: Book): Boolean =
        localDataSource.observeBy(book.isbn).first() != null

    override suspend fun resetCache() {
        cacheLock.withLock {
            cache.clear()

            currentPage = 1
            lastKey = null
        }
    }
}
