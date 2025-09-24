package com.booksearch.assignment.data.mapper

import com.booksearch.assignment.data.local.entity.BookEntity
import com.booksearch.assignment.data.remote.dto.BookDto
import com.booksearch.assignment.domain.model.Book

internal fun effectivePrice(price: Int, salePrice: Int): Int =
    if (salePrice > 0) salePrice else price

fun Book.toEntity(): BookEntity = BookEntity(
    isbn = isbn,
    title = title,
    contents = contents,
    url = url,
    datetime = datetime,
    authors = authorList,
    publisher = publisher,
    translators = translatorList,
    price = price,
    salePrice = salePrice,
    thumbnail = thumbnail,
    status = status,
    effectivePrice = effectivePrice(price, salePrice)
)

fun BookDto.toDomain(isBookmarked: Boolean = false): Book = Book(
    title = title,
    contents = contents,
    url = url,
    isbn = isbn,
    datetime = datetime,
    authorList = authors,
    publisher = publisher,
    translatorList = translators,
    price = price,
    salePrice = salePrice,
    thumbnail = thumbnail,
    status = status,
    isBookmarked = isBookmarked
)

fun BookDto.toEntity(): BookEntity = BookEntity(
    isbn = isbn,
    title = title,
    contents = contents,
    url = url,
    datetime = datetime,
    authors = authors,
    publisher = publisher,
    translators = translators,
    price = price,
    salePrice = salePrice,
    thumbnail = thumbnail,
    status = status,
    effectivePrice = effectivePrice(price, salePrice)
)

fun BookEntity.toDomain(): Book = Book(
    title = title,
    contents = contents,
    url = url,
    isbn = isbn,
    datetime = datetime,
    authorList = authors,
    publisher = publisher,
    translatorList = translators,
    price = price,
    salePrice = salePrice,
    thumbnail = thumbnail,
    status = status,
    isBookmarked = true
)
