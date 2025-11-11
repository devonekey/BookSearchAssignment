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
 * 도서들을 검색하는 유즈케이스
 */
class SearchBooksUseCase @Inject constructor(
    private val repository: BookRepository
) : UseCase<SearchBooksQuery, Flow<BooksResult>> {
    /**
     * 도서들을 검색하는 유즈케이스 실행 함수
     *
     * @param input     [도서 검색 쿼리][SearchBooksQuery]
     * @return          [도서 검색 결과][BooksResult]를 비동기 스트림으로 방출하는 Flow
     *   - [Loading]:   검색 실행 중
     *   - [Success]:   검색 성공, 검색된 [도서 목록][com.booksearch.assignment.domain.model.Books] 포함
     *   - [Failure]:   검색 실패, 에러 코드 포함
     */
    override suspend fun invoke(input: SearchBooksQuery): Flow<BooksResult> =
        flow {
            val books = repository.searchBooks(query = input)

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
