package com.booksearch.assignment.domain.usecase

import com.booksearch.assignment.domain.model.input.GetBookmarkedBooksQuery
import com.booksearch.assignment.domain.model.output.BooksResult
import com.booksearch.assignment.domain.model.output.BooksResult.Failure
import com.booksearch.assignment.domain.model.output.BooksResult.Failure.Code
import com.booksearch.assignment.domain.model.output.BooksResult.Loading
import com.booksearch.assignment.domain.model.output.BooksResult.Success
import com.booksearch.assignment.domain.repository.BookRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * 북마크된 도서들을 가져오는 유즈케이스
 */
class GetBookmarkedBooksUseCase @Inject constructor(
    private val repository: BookRepository
) : UseCase<GetBookmarkedBooksQuery, Flow<BooksResult>> {
    /**
     * 북마크 도서들을 가져오는 유즈케이스 실행 함수
     *
     * @param input     북마크 [도서 조회 쿼리][GetBookmarkedBooksQuery]
     * @return          북마크 [도서 조회 결과][BooksResult]를 비동기 스트림으로 방출하는 Flow
     *   - [Loading]:   조회 진행 중
     *   - [Success]:   북마크 도서 조회 성공, 조회된 북마크 [도서 목록][com.booksearch.assignment.domain.model.Books] 포함
     *   - [Failure]:   북마크 도서 조회 실패, 에러 코드 포함
     */
    override suspend fun invoke(input: GetBookmarkedBooksQuery): Flow<BooksResult> =
        repository.getBookmarkedBooks(query = input)
            .map { books ->
                if (books.isNotEmpty()) {
                    Success(books = books)
                } else {
                    Failure(code = Code.NOT_FOUND)
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
