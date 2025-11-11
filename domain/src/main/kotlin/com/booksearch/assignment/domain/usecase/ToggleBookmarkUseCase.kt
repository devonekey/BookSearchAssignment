package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.input.ToggleBookmarkTarget
import com.booksearch.assignment.domain.model.output.ToggleBookmarkResult
import com.booksearch.assignment.domain.model.output.ToggleBookmarkResult.Added
import com.booksearch.assignment.domain.model.output.ToggleBookmarkResult.Removed
import com.booksearch.assignment.domain.repository.BookRepository
import javax.inject.Inject

/**
 * 도서를 북마크하거나 북마크를 제거하는 유즈케이스
 */
class ToggleBookmarkUseCase @Inject constructor(
    private val repository: BookRepository
) : UseCase<ToggleBookmarkTarget, ToggleBookmarkResult> {
    /**
     * 도서를 북마크하거나 북마크를 제거하는 유즈케이스 실행 함수
     *
     * @param input     [북마크를 토글할 대상 도서][ToggleBookmarkTarget]
     * @return          [북마크 토글 결과][ToggleBookmarkResult]
     *   - [Added]:     북마크 추가 완료
     *   - [Removed]:   북마크 제거 완료
     */
    override suspend fun invoke(input: ToggleBookmarkTarget): ToggleBookmarkResult =
        if (repository.isBookmark(book = input.book)) {
            repository.removeBookmark(book = input.book)
            Removed
        } else {
            repository.addBookmark(book = input.book)
            Added
        }
}
