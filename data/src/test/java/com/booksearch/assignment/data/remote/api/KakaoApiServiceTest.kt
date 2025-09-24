package com.booksearch.assignment.data.remote.api

import com.booksearch.assignment.data.di.NetworkModule
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KakaoApiServiceTest {
    private lateinit var server: MockWebServer
    private lateinit var apiService: KakaoApiService

    @BeforeAll
    fun beforeAll() {
        server = MockWebServer()

        server.start()

        apiService = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(NetworkModule.provideOkHttpClient("TEST_API_KEY"))
            .addConverterFactory(GsonConverterFactory.create(NetworkModule.provideGson()))
            .build()
            .create(KakaoApiService::class.java)
    }

    @Test
    fun `쿼리_파라미터들이_path에_포함되고_응답_결과는_파싱된다`() = runTest {
        // Given
        val body = """
            {
              "meta": {
                "total_count": 1217,
                "pageable_count": 1000,
                "is_end": false
              },
              "documents": [
                {
                  "title": "THE ANDROID DEVLOPER S COOKBOOK(한국어판)",
                  "contents": "『The Android Developer's Cookbook 한국어판』은 초보 개발자가 즉시 안드로이드 애플리케이션을 개발할 수 있는 능력을 갖출 수 있도록 도와 주는 책이다...",
                  "url": "https://search.daum.net/search?w=bookpage&bookId=4922747&q=Rxjava+for+Android+Developers",
                  "isbn": "896077183X 9788960771833",
                  "datetime": "2011-02-23T00:00:00.000+09:00",
                  "authors": ["제임스 스틸", "넬슨 토"],
                  "publisher": "에이콘출판",
                  "translators": ["장재현"],
                  "price": 30000,
                  "sale_price": 27000,
                  "thumbnail": "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F837792%3Ftimestamp%3D20220902173044",
                  "status": "정상판매"
                }
              ]
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(body))

        // When
        val resp = apiService.searchBooks(
            query = "Android",
            sort = "latest",
            page = 1,
            size = 20,
            target = "title"
        )

        // Then: 쿼리 파라미터들이 path에 포함되는지 확인
        assertTrue(resp.isSuccessful)

        val request = server.takeRequest()

        assertEquals(
            "/v3/search/book?query=Android&sort=latest&page=1&size=20&target=title",
            request.path
        )

        // Then: 응답 결과가 정상적으로 파싱되는지 확인
        val bookResponseDto = resp.body()

        assertNotNull(bookResponseDto)
        assertEquals(1217, bookResponseDto?.meta?.totalCount)
        assertEquals(1000, bookResponseDto?.meta?.pageableCount)
        assertEquals(false, bookResponseDto?.meta?.isEnd)
        assertEquals(1, bookResponseDto?.documents?.size)

        val bookDto = bookResponseDto?.documents?.first()

        assertEquals("THE ANDROID DEVLOPER S COOKBOOK(한국어판)", bookDto?.title)
        assertEquals("896077183X 9788960771833", bookDto?.isbn)
        assertEquals(30_000, bookDto?.price)
        assertEquals(27_000, bookDto?.salePrice)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.getDefault())
        val parsed = bookDto?.datetime

        assertEquals("2011-02-23 00:00:00.000+0900", dateFormat.format(parsed))
    }

    @Test
    fun `필수가_아닌_쿼리_파라미터에_값을_주지_않으면_path에_포함되지_않는다`() = runTest {
        // Given
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        // When
        val response = apiService.searchBooks(query = "Android")

        // Then
        assertTrue(response.isSuccessful)

        val recorded = server.takeRequest()

        assertEquals("/v3/search/book?query=Android", recorded.path)
    }

    @AfterAll
    fun afterAll() {
        server.shutdown()
    }
}