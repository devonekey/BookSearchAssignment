package com.booksearch.assignment.domain.repository

import com.booksearch.assignment.domain.model.Book
import com.booksearch.assignment.domain.model.query.BookmarkSort
import com.booksearch.assignment.domain.model.query.SearchSort
import com.booksearch.assignment.domain.model.query.SearchTarget
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    /**
     * 도서 목록을 가져오는 함수
     *
     * @param query  검색을 원하는 질의어
     * @param sort   결과 문서 정렬 방식, accuracy(정확도순) 또는 latest(발간일순), 기본값 accuracy
     * @param page   결과 페이지 번호, 1~50 사이의 값, 기본 값 1
     * @param size   한 페이지에 보여질 문서 수, 1~50 사이의 값, 기본 값 10
     * @param target 검색 필드 제한, 사용 가능한 값: title(제목), isbn (ISBN), publisher(출판사), person(인명)
     * @return 검색된 도서 목록
     */
    suspend fun searchBooks(
        query: String? = null,
        sort: SearchSort = SearchSort.ACCURACY,
        page: Int = 1,
        size: Int = 20,
        target: SearchTarget? = null
    ): List<Book>

    /**
     * 북마크된 도서 목록을 가져오는 함수
     *
     * @param query      검색을 원하는 질의어
     * @param sort       결과 정렬 방식, titleAscending(제목 오름차순) 또는 titleDescending(제목 내림차순)
     * @param priceRange 금액 범위
     * @return 북마크된 도서 목록
     */
    fun getBookmarkedBooks(
        query: String? = null,
        sort: BookmarkSort = BookmarkSort.TITLE_ASCENDING,
        priceRange: IntRange = IntRange(
            start = 0,
            endInclusive = Int.MAX_VALUE
        )
    ): Flow<List<Book>>

    /**
     * 도서를 북마크하는 함수
     *
     * @param book 북마크할 도서
     */
    suspend fun addBookmark(book: Book)

    /**
     * 도서로부터 북마크를 제거하는 함수
     *
     * @param book 북마크를 제거할 도서
     */
    suspend fun removeBookmark(book: Book)

    /**
     * 북마크된 도서인지 확인하는 함수
     *
     * @param book 북마크됐는지 확인할 도서
     * @return 북마크된 도서인지 여부
     */
    suspend fun isBookmark(book: Book): Boolean
}
