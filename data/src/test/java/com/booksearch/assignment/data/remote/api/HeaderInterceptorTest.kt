package com.booksearch.assignment.data.remote.api

import com.booksearch.assignment.data.di.NetworkModule
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HeaderInterceptorTest {
    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @BeforeEach
    fun setup() {
        server = MockWebServer()
        client = NetworkModule.provideOkHttpClient("TEST_API_KEY")

        server.start()
    }

    @Test
    fun `헤더에_API_Key가_추가된다`() {
        // Given
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val request = Request.Builder()
            .url(server.url("/"))
            .build()

        // When
        client.newCall(request)
            .execute()
            .close()

        // Then
        val recorded = server.takeRequest()

        assertEquals("KakaoAK TEST_API_KEY", recorded.getHeader("Authorization"))
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }
}
