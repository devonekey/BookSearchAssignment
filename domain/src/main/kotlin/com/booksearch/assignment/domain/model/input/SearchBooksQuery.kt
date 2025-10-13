package com.booksearch.assignment.domain.model.input

/**
 * 도서들을 검색할 때 요구되는 질의
 *
 * @property keyword    검색어
 * @property sort       결과 정렬 방식
 * @property page       결과 페이지 번호
 * @property size       한 페이지에 보여질 문서 수
 * @property target     검색어 적용 범위
 */
data class SearchBooksQuery(
    val keyword: String? = null,
    val sort: Sort? = null,
    val page: Int? = null,
    val size: Int? = null,
    val target: Target? = null
) {
    /**
     * 결과 정렬 방식
     *
     * - `ACCURACY`:    정확도순(기본값)
     * - `LATEST`:      발간일순
     */
    enum class Sort {
        ACCURACY, LATEST;

        fun toParam(): String = when (this) {
            ACCURACY -> "accuracy"
            LATEST -> "latest"
        }
    }

    /**
     * 검색어 적용 범위
     *
     * - `TITLE`:       제목
     * - `ISBN`:        ISBN
     * - `PUBLISHER`:   출판사
     * - `PERSON`:      인명
     */
    enum class Target {
        TITLE, ISBN, PUBLISHER, PERSON;

        fun toParam() = when (this) {
            TITLE -> "title"
            ISBN -> "isbn"
            PUBLISHER -> "publisher"
            PERSON -> "person"
        }
    }
}
