package com.booksearch.assignment.domain.model

import com.booksearch.assignment.domain.util.calculateEffectivePrice
import java.util.Date

/**
 * 도서 모델
 *
 * @property title          도서 제목
 * @property contents       도서 소개
 * @property url            도서 상세 URL
 * @property isbn           국제 표준 도서번호
 * @property datetime       도서 출판날짜
 * @property authors        도서 저자 목록
 * @property publisher      도서 출판사
 * @property translators    도서 번역자 목록
 * @property price          도서 정가
 * @property salePrice      도서 판매가
 * @property thumbnail      도서 표지 미리보기 URL
 * @property status         도서 판매 상태 정보(정상, 품절, 절판 등)
 * @property isBookmarked   북마크 여부
 * @property effectivePrice 최종 판매가
 */
data class Book(
    val title: String = "",
    val contents: String = "",
    val url: String = "",
    val isbn: String = "",
    val datetime: Date = Date(),
    val authors: List<String> = emptyList(),
    val publisher: String = "",
    val translators: List<String> = emptyList(),
    val price: Int = -1,
    val salePrice: Int = -1,
    val thumbnail: String = "",
    val status: String = "",
    val isBookmarked: Boolean = false
) {
    val effectivePrice: Int
        get() = calculateEffectivePrice(price = price, salePrice = salePrice)

    override fun equals(other: Any?): Boolean =
        other is Book && hashCode() == other.hashCode()

    override fun hashCode(): Int =
        isbn.hashCode()
}
