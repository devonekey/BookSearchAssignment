package com.booksearch.assignment.domain.model.input

/**
 * 북마크된 도서들을 가져올 때 요구되는 질의
 *
 * @property keyword    검색어
 * @property sort       결과 정렬 방식
 * @property priceRange 금액 범위
 */
data class GetBookmarkedBooksQuery(
    val keyword: String? = null,
    val sort: Sort? = null,
    val priceRange: IntRange? = null
) {
    /**
     * 결과 정렬 방식
     *
     * - `TITLE_ASCENDING`:     제목 오름차순(기본값)
     * - `TITLE_DESCENDING`:    제목 내림차순
     */
    enum class Sort {
        TITLE_ASCENDING, TITLE_DESCENDING;

        fun toParam(): String = when (this) {
            TITLE_ASCENDING -> "titleAscending"
            TITLE_DESCENDING -> "titleDescending"
        }
    }
}
