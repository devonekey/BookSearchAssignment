package com.booksearch.assignment.di

import com.booksearch.assignment.data.di.ApiKey
import com.booksearch.assignment.data.di.BaseUrl
import com.booksearch.assignment.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkConfigModule {

    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.KAKAO_BASE_URL

    @Provides
    @Singleton
    @ApiKey
    fun provideApiKey(): String = BuildConfig.KAKAO_REST_API_KEY
}
