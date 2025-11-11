package com.booksearch.assignment.domain.repository

import com.booksearch.assignment.domain.model.Book
import com.booksearch.assignment.domain.model.Books
import com.booksearch.assignment.domain.model.input.GetBookmarkedBooksQuery
import com.booksearch.assignment.domain.model.input.SearchBooksQuery
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    /**
     * 도서 목록을 검색하는 함수
     *
     * @param query 도서 검색에 필요한 질의
     * @return      검색된 도서 목록
     */
    suspend fun searchBooks(query: SearchBooksQuery): Books

    /**
     * 검색된 도서 목록을 초기화하는 함수
     */
    suspend fun clearSearchedBooks()

    /**
     * 북마크된 도서 목록을 가져오는 함수
     *
     * @param query 북마크된 도서들을 가져올 때 필요한 질의
     * @return      북마크된 도서 목록
     */
    fun getBookmarkedBooks(query: GetBookmarkedBooksQuery): Flow<Books>

    /**
     * 북마크된 도서 목록을 초기화하는 함수
     */
    suspend fun clearBookmarkedBooks()

    /**
     * 도서를 북마크하는 함수
     *
     * @param book  북마크할 도서
     */
    suspend fun addBookmark(book: Book)

    /**
     * 도서로부터 북마크를 제거하는 함수
     *
     * @param book  북마크를 제거할 도서
     */
    suspend fun removeBookmark(book: Book)

    /**
     * 북마크된 도서인지 확인하는 함수
     *
     * @param book  북마크됐는지 확인할 도서
     * @return      북마크된 도서인지 여부
     */
    suspend fun isBookmark(book: Book): Boolean
}
