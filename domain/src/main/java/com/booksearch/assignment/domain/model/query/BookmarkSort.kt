package com.booksearch.assignment.domain.model.query

enum class BookmarkSort {
    TITLE_ASCENDING, TITLE_DESCENDING;

    fun toParam(): String = when (this) {
        TITLE_ASCENDING -> "titleAscending"
        TITLE_DESCENDING -> "titleDescending"
    }
}
