package com.booksearch.assignment.data.remote.api

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor(
    private val restApiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "KakaoAK $restApiKey")
            .build()

        return chain.proceed(request)
    }
}
