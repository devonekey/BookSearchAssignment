package com.booksearch.assignment.data.local

import com.booksearch.assignment.data.local.dao.BookDao
import com.booksearch.assignment.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSourceImpl @Inject constructor(
    private val dao: BookDao
) : LocalDataSource {

    override suspend fun upsert(book: BookEntity) {
        dao.upsert(book)
    }

    override suspend fun upsertAll(books: List<BookEntity>) {
        dao.upsertAll(books)
    }

    override suspend fun deleteBy(isbn: String) {
        dao.deleteBy(isbn)
    }

    override fun observeBy(isbn: String): Flow<BookEntity?> =
        dao.observeBy(isbn)

    override fun observeBookmarksOrderByTitleAsc(
        minPrice: Int?,
        maxPrice: Int?
    ): Flow<List<BookEntity>> =
        dao.observeBookmarkOrderByTitleAsc(
            query = null, // 텍스트 검색을 사용하지 않기로 했던 결정 반영
            minPrice = minPrice,
            maxPrice = maxPrice
        )

    override fun observeBookmarksOrderByTitleDesc(
        minPrice: Int?,
        maxPrice: Int?
    ): Flow<List<BookEntity>> =
        dao.observeBookmarkOrderByTitleDesc(
            query = null,
            minPrice = minPrice,
            maxPrice = maxPrice
        )

    override fun observeBookmarkedIsbnSet(): Flow<Set<String>> =
        dao.observeBookmarkOrderByTitleAsc(
            query = null,
            minPrice = null,
            maxPrice = null
        ).map { list ->
            list.asSequence()
                .map { it.isbn }
                .toSet()
        }
}