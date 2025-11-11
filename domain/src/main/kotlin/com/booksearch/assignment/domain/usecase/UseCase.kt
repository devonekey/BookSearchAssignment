package com.booksearch.assignment.domain.usecase

/**
 * 유즈케이스
 */
interface UseCase<in I, out O> {
    /**
     * 주어진 입력으로 유즈케이스를 실행하고 결과를 출력하는 함수
     *
     * @param input 입력
     * @return      출력
     */
    suspend operator fun invoke(input: I): O
}
