package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.input.GetBookmarkedBooksQuery
import com.booksearch.assignment.domain.model.output.BooksResult
import com.booksearch.assignment.domain.model.output.BooksResult.Failure
import com.booksearch.assignment.domain.model.output.BooksResult.Failure.Code
import com.booksearch.assignment.domain.model.output.BooksResult.Loading
import com.booksearch.assignment.domain.model.output.BooksResult.Success
import com.booksearch.assignment.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * 가져온 북마크된 도서들을 초기화하고 다시 가져오는 유즈케이스
 */
class ResetBookmarkedBooksUseCase @Inject constructor(
    private val repository: BookRepository
) : UseCase<Unit, Flow<BooksResult>> {
    /**
     * 가져온 북마크된 도서들을 초기화하고 다시 가져오는 유즈케이스 실행 함수
     *
     * @param input     사용되지 않음
     * @return          초기화된 북마크 [도서 조회 결과][BooksResult]를 비동기 스트림으로 방출하는 Flow
     *   - [Loading]:   초기화 및 재조회 실행 중
     *   - [Success]:   북마크 도서 재조회 성공, 초기화 후 재조회된 북마크 [도서 목록][com.booksearch.assignment.domain.model.Books] 포함
     *   - [Failure]:   북마크 도서 재조회 실패, 에러 코드 포함
     */
    override suspend fun invoke(input: Unit): Flow<BooksResult> {
        repository.clearBookmarkedBooks()

        return repository.getBookmarkedBooks(query = GetBookmarkedBooksQuery(keyword = null))
            .map { books ->
                if (books.isNotEmpty()) {
                    Success(books = books)
                } else {
                    Failure(code = Code.NOT_FOUND)
                }
            }.onStart {
                emit(Loading)
            }.catch { e ->
                emit(Failure(code = Code.UNKNOWN))
            }
    }
}
