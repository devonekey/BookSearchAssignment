package com.booksearch.assignment.domain.model.input

import com.booksearch.assignment.domain.model.Book

/**
 * 도서를 북마크하거나 북마크를 제거하는 행위에 대한 입력
 *
 * @property book   토글하려는 도서
 */
data class ToggleBookmarkTarget(val book: Book)
