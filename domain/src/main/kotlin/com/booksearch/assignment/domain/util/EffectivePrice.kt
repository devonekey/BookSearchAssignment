package com.booksearch.assignment.domain.util

/**
 * 도서의 최종 판매가를 계산하는 함수
 *
 * @param price     도서 정가
 * @param salePrice 도서 판매가
 * @return          최종 판매가
 */
fun calculateEffectivePrice(price: Int, salePrice: Int): Int =
    salePrice.takeIf { it > 0 }
        ?: price.takeIf { it > 0 }
        ?: 0
