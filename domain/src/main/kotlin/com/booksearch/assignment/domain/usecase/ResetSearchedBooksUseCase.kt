package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.input.SearchBooksQuery
import com.booksearch.assignment.domain.model.output.BooksResult
import com.booksearch.assignment.domain.model.output.BooksResult.Failure
import com.booksearch.assignment.domain.model.output.BooksResult.Failure.Code
import com.booksearch.assignment.domain.model.output.BooksResult.Loading
import com.booksearch.assignment.domain.model.output.BooksResult.Success
import com.booksearch.assignment.domain.repository.BookRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * 검색된 도서들을 초기화하고 다시 검색하는 유즈케이스
 */
class ResetSearchedBooksUseCase @Inject constructor(
    private val repository: BookRepository
) : UseCase<Unit, Flow<BooksResult>> {
    /**
     * 검색된 도서들을 초기화하고 다시 검색하는 유즈케이스 실행 함수
     *
     * @param input     사용되지 않음
     * @return          초기화된 [도서 검색 결과][BooksResult]를 비동기 스트림으로 방출하는 Flow
     *   - [Loading]:   초기화 및 재검색 실행 중
     *   - [Success]:   도서 재검색 성공, 초기화 후 재검색된 [도서 목록][com.booksearch.assignment.domain.model.Books] 포함
     *   - [Failure]:   도서 재검색 실패, 에러 코드 포함
     */
    override suspend fun invoke(input: Unit): Flow<BooksResult> =
        flow {
            repository.clearSearchedBooks()

            val books = repository.searchBooks(query = SearchBooksQuery(page = 1))

            if (books.isNotEmpty()) {
                emit(Success(books = books))
            } else {
                emit(Failure(code = Code.NOT_FOUND))
            }
        }.onStart {
            emit(Loading)
        }.catch { e ->
            if (e is CancellationException) {
                throw e
            }

            emit(Failure(code = Code.UNKNOWN))
        }
}
