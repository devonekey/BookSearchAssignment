package com.booksearch.assignment.data.remote

import com.booksearch.assignment.data.di.NetworkModule
import com.booksearch.assignment.data.remote.api.KakaoApiService
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RemoteDataSourceImplTest {
    private lateinit var server: MockWebServer
    private lateinit var service: KakaoApiService
    private lateinit var remoteDataSource: RemoteDataSource

    @BeforeAll
    fun setup() {
        server = MockWebServer()

        server.start()

        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(NetworkModule.provideOkHttpClient("TEST_API_KEY"))
            .addConverterFactory(GsonConverterFactory.create(NetworkModule.provideGson()))
            .build()
            .create(KakaoApiService::class.java)
        remoteDataSource = RemoteDataSourceImpl(service)
    }

    @Test
    fun `API를_호출하고_성공을_응답받으면_BookResponseDto를_받는다`() = runTest {
        // Given
        val body = """
            {
              "meta": { "total_count": 1217, "pageable_count": 1000, "is_end": false },
              "documents": []
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        // When
        val result = remoteDataSource.searchBooks(query = "Android")

        // Then
        assertNotNull(result)
        assertEquals(1217, result.meta.totalCount)
    }

    @Test
    fun `API를_호출하고_실패를_응답받으면_HttpException을_던진다`() = runTest {
        // Given
        server.enqueue(MockResponse().setResponseCode(400))

        // Expect
        assertThrows<HttpException> {
            remoteDataSource.searchBooks("Android")
        }
    }

    @Test
    fun `API를_호출하고_성공을_응답받았으나_받는_것이_없으면_IOException을_던진다`() = runTest {
        // Given
        server.enqueue(MockResponse().setResponseCode(200).setBody(""))

        // Expect
        assertThrows<IOException> {
            remoteDataSource.searchBooks("Android")
        }
    }

    @AfterAll
    fun tearDown() {
        server.shutdown()
    }
}
