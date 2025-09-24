package com.booksearch.assignment.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.booksearch.assignment.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(books: List<BookEntity>)

    @Delete
    suspend fun delete(book: BookEntity)

    @Query("DELETE FROM books WHERE isbn = :isbn")
    suspend fun deleteBy(isbn: String)

    @Query("SELECT * FROM books WHERE isbn = :isbn LIMIT 1")
    fun observeBy(isbn: String): Flow<BookEntity?>

    @Query("""
        SELECT * FROM books
        WHERE (:query IS NULL OR :query = '' 
               OR title      LIKE '%' || :query || '%'
               OR publisher  LIKE '%' || :query || '%'
               OR authors    LIKE '%' || :query || '%')
          AND (:minPrice IS NULL OR effectivePrice >= :minPrice)
          AND (:maxPrice IS NULL OR effectivePrice <= :maxPrice)
        ORDER BY title COLLATE NOCASE ASC
    """)
    fun observeBookmarkOrderByTitleAsc(
        query: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null
    ): Flow<List<BookEntity>>

    @Query("""
        SELECT * FROM books
        WHERE (:query IS NULL OR :query = '' 
               OR title      LIKE '%' || :query || '%'
               OR publisher  LIKE '%' || :query || '%'
               OR authors    LIKE '%' || :query || '%')
          AND (:minPrice IS NULL OR effectivePrice >= :minPrice)
          AND (:maxPrice IS NULL OR effectivePrice <= :maxPrice)
        ORDER BY title COLLATE NOCASE DESC
    """)
    fun observeBookmarkOrderByTitleDesc(
        query: String? = null,
        minPrice: Int? = null,
        maxPrice: Int? = null
    ): Flow<List<BookEntity>>
}
