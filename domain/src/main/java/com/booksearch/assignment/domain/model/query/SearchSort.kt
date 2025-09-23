package com.booksearch.assignment.domain.model.query

enum class SearchSort {
    ACCURACY, LATEST;

    fun toParam(): String = when (this) {
        ACCURACY -> "accuracy"
        LATEST -> "latest"
    }
}
