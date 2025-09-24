package com.booksearch.assignment.data.di

import com.booksearch.assignment.data.remote.api.HeaderInterceptor
import com.booksearch.assignment.data.remote.api.KakaoApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApiKey apiKey: String
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor(apiKey))
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            .create()

    @Provides
    @Singleton
    fun provideRetrofit(
        @BaseUrl baseUrl: String,
        client: OkHttpClient,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideKakaoApiService(retrofit: Retrofit): KakaoApiService =
        retrofit.create(KakaoApiService::class.java)
}
