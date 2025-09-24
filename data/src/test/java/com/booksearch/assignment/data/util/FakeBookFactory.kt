package com.booksearch.assignment.data.util

import com.booksearch.assignment.data.local.entity.BookEntity
import com.booksearch.assignment.data.remote.dto.BookDto
import com.booksearch.assignment.domain.model.Book
import java.text.SimpleDateFormat
import java.util.Locale

private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())

fun fakeBook() = Book(
    title = "안드로이드 포렌식(에이콘 디지털 포렌식 시리즈 3)",
    contents = "『안드로이드 포렌식』은 안드로이드의 핵심적인 하드웨어와 소프트웨어 요소, 파일시스템, 데이터 구조, 데이터 보안을 위한 고려사항 등 안드로이드 플랫폼에 대한 철저한 설명과 포렌식 데이터 획득 기술, 그것을 분석하기 위해 필요한 전략을 제시한다...",
    url = "https://search.daum.net/search?w=bookpage&bookId=838144&q=%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C+%ED%8F%AC%EB%A0%8C%EC%8B%9D%28%EC%97%90%EC%9D%B4%EC%BD%98+%EB%94%94%EC%A7%80%ED%84%B8+%ED%8F%AC%EB%A0%8C%EC%8B%9D+%EC%8B%9C%EB%A6%AC%EC%A6%88+3%29",
    isbn = "8960774030 9788960774032",
    datetime = simpleDateFormat.parse("2013-02-28T00:00:00.000+09:00"),
    authorList = listOf("앤드류 후그"),
    publisher = "에이콘출판",
    translatorList = listOf("윤근용"),
    price = 35000,
    salePrice = 31500,
    thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F838144%3Ftimestamp%3D20221025123556",
    status = "정상판매",
    isBookmarked = false
)

fun fakeBookDto() = BookDto(
    title = "안드로이드 포렌식(에이콘 디지털 포렌식 시리즈 3)",
    contents = "『안드로이드 포렌식』은 안드로이드의 핵심적인 하드웨어와 소프트웨어 요소, 파일시스템, 데이터 구조, 데이터 보안을 위한 고려사항 등 안드로이드 플랫폼에 대한 철저한 설명과 포렌식 데이터 획득 기술, 그것을 분석하기 위해 필요한 전략을 제시한다...",
    url = "https://search.daum.net/search?w=bookpage&bookId=838144&q=%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C+%ED%8F%AC%EB%A0%8C%EC%8B%9D%28%EC%97%90%EC%9D%B4%EC%BD%98+%EB%94%94%EC%A7%80%ED%84%B8+%ED%8F%AC%EB%A0%8C%EC%8B%9D+%EC%8B%9C%EB%A6%AC%EC%A6%88+3%29",
    isbn = "8960774030 9788960774032",
    datetime = simpleDateFormat.parse("2013-02-28T00:00:00.000+09:00"),
    authors = listOf("앤드류 후그"),
    publisher = "에이콘출판",
    translators = listOf("윤근용"),
    price = 35000,
    salePrice = 31500,
    thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F838144%3Ftimestamp%3D20221025123556",
    status = "정상판매"
)

fun fakeBookEntity() = BookEntity(
    title = "안드로이드 포렌식(에이콘 디지털 포렌식 시리즈 3)",
    contents = "『안드로이드 포렌식』은 안드로이드의 핵심적인 하드웨어와 소프트웨어 요소, 파일시스템, 데이터 구조, 데이터 보안을 위한 고려사항 등 안드로이드 플랫폼에 대한 철저한 설명과 포렌식 데이터 획득 기술, 그것을 분석하기 위해 필요한 전략을 제시한다...",
    url = "https://search.daum.net/search?w=bookpage&bookId=838144&q=%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C+%ED%8F%AC%EB%A0%8C%EC%8B%9D%28%EC%97%90%EC%9D%B4%EC%BD%98+%EB%94%94%EC%A7%80%ED%84%B8+%ED%8F%AC%EB%A0%8C%EC%8B%9D+%EC%8B%9C%EB%A6%AC%EC%A6%88+3%29",
    isbn = "8960774030 9788960774032",
    datetime = simpleDateFormat.parse("2013-02-28T00:00:00.000+09:00"),
    authors = listOf("앤드류 후그"),
    publisher = "에이콘출판",
    translators = listOf("윤근용"),
    price = 35000,
    salePrice = 31500,
    thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F838144%3Ftimestamp%3D20221025123556",
    status = "정상판매",
    effectivePrice = 31500
)
