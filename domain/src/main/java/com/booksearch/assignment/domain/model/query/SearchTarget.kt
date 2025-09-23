package com.booksearch.assignment.domain.model.query

enum class SearchTarget {
    TITLE, ISBN, PUBLISHER, PERSON;

    fun toParam() = when (this) {
        TITLE -> "title"
        ISBN -> "isbn"
        PUBLISHER -> "publisher"
        PERSON -> "person"
    }
}
