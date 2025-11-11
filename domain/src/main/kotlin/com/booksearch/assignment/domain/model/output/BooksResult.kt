package com.booksearch.assignment.domain.model.output

import com.booksearch.assignment.domain.model.Books

/**
 * 도서들을 검색하는 행위에 대한 결과
 */
sealed interface BooksResult {
    /**
     * 실행 중
     */
    data object Loading : BooksResult

    /**
     * 성공
     *
     * @property books  검색 결과
     */
    data class Success(val books: Books) : BooksResult

    /**
     * 실패
     *
     * @property message    메시지
     */
    data class Failure(
        val code: Code,
        val message: String? = null
    ) : BooksResult {
        enum class Code {
            NOT_FOUND,
            UNKNOWN
        }
    }
}
