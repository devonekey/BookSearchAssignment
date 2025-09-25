package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.repository.BookRepository
import javax.inject.Inject

class ResetUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke() {
        repository.resetCache()
    }
}
