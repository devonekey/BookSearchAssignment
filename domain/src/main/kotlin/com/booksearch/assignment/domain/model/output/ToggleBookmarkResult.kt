package com.booksearch.assignment.domain.model.output

/**
 * 도서를 북마크하거나 북마크를 제거하는 행위에 대한 출력
 */
sealed interface ToggleBookmarkResult {
    /**
     * 북마크 추가 완료
     */
    data object Added : ToggleBookmarkResult

    /**
     * 북마크 제거 완료
     */
    data object Removed : ToggleBookmarkResult
}
