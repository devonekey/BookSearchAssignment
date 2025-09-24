package com.booksearch.assignment.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseName

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InMemory
