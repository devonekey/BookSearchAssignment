package com.booksearch.assignment.data.remote

import com.booksearch.assignment.data.remote.api.KakaoApiService
import com.booksearch.assignment.data.remote.dto.BookResponseDto
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val kakaoApiService: KakaoApiService
) : RemoteDataSource {
    override suspend fun searchBooks(
        query: String,
        sort: String?,
        page: Int?,
        size: Int?,
        target: String?
    ): BookResponseDto {
        val response = kakaoApiService.searchBooks(
            query = query,
            sort = sort,
            page = page,
            size = size,
            target = target
        )

        if (!response.isSuccessful) {
            throw HttpException(response)
        }

        return response.body() ?: throw IOException("Empty response body")
    }
}
