package com.booksearch.assignment.domain.repository

import com.booksearch.assignment.domain.model.Book
import com.booksearch.assignment.domain.model.Books
import com.booksearch.assignment.domain.model.input.GetBookmarkedBooksQuery
import com.booksearch.assignment.domain.model.input.SearchBooksQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Locale

class FakeBookRepositoryImpl : BookRepository {
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    private val searchBooks = fakeBook().toMutableList()
    private val bookmarkedBooks = fakeBook().toMutableList()
    private var lastSearchBooksQuery: SearchBooksQuery? = null
    private var lastGetBookmarkedBooksQuery: GetBookmarkedBooksQuery? = null

    override suspend fun searchBooks(query: SearchBooksQuery): Books {
        var keyword = query.keyword ?: lastSearchBooksQuery?.keyword
        var sort = query.sort ?: lastSearchBooksQuery?.sort ?: SearchBooksQuery.Sort.ACCURACY
        var page = query.page ?: ((lastSearchBooksQuery?.page ?: 0) + 1)
        var size = query.size ?: lastSearchBooksQuery?.size ?: 10
        var target = query.target ?: lastSearchBooksQuery?.target
        val isChanged = keyword != lastSearchBooksQuery?.keyword
                || sort != (lastSearchBooksQuery?.sort ?: SearchBooksQuery.Sort.ACCURACY)
                || target != lastSearchBooksQuery?.target

        if (isChanged) {
            keyword = query.keyword
            sort = query.sort ?: SearchBooksQuery.Sort.ACCURACY
            page = query.page ?: 1
            size = query.size ?: 10
            target = query.target
        }

        if (keyword.isNullOrBlank()) {
            return Books(data = emptyList())
        }

        lastSearchBooksQuery = SearchBooksQuery(
            keyword = keyword,
            sort = sort,
            page = page,
            size = size,
            target = target,
        )
        val filtered = searchBooks.filter { book ->
            val other = when (target) {
                SearchBooksQuery.Target.TITLE -> book.title
                SearchBooksQuery.Target.ISBN -> book.isbn
                SearchBooksQuery.Target.PUBLISHER -> book.publisher
                SearchBooksQuery.Target.PERSON -> book.authors.joinToString()
                else -> keyword
            }

            keyword.contains(other = other, ignoreCase = true)
                    || other.contains(other = keyword , ignoreCase = true)
        }
        val sorted = when (sort) {
            SearchBooksQuery.Sort.ACCURACY -> filtered
            SearchBooksQuery.Sort.LATEST -> filtered.sortedByDescending { it.datetime.time }
        }
        val fromIndex = (page - 1) * size
        val toIndex = (page - 1) * size + size
        val picked = when {
            fromIndex in sorted.indices && toIndex in sorted.indices ->
                sorted.subList(fromIndex, toIndex)
            fromIndex in sorted.indices -> sorted.subList(fromIndex, sorted.size)
            else -> emptyList()
        }

        return Books(data = picked, isEnd = toIndex >= sorted.size)
    }

    override suspend fun clearSearchedBooks() {
    }

    override fun getBookmarkedBooks(query: GetBookmarkedBooksQuery): Flow<Books> {
        lastGetBookmarkedBooksQuery = when {
            lastGetBookmarkedBooksQuery == null -> query
            lastGetBookmarkedBooksQuery != null && query == GetBookmarkedBooksQuery() ->
                lastGetBookmarkedBooksQuery
            else -> query
        }
        val keyword = lastGetBookmarkedBooksQuery?.keyword
        val sort = lastGetBookmarkedBooksQuery?.sort ?: GetBookmarkedBooksQuery.Sort.TITLE_ASCENDING
        val priceRange = lastGetBookmarkedBooksQuery?.priceRange ?: IntRange(0, Int.MAX_VALUE)
        val filtered = bookmarkedBooks.filter { book ->
            if (keyword.isNullOrBlank()) {
                return@filter true
            }

            keyword.contains(other = book.title, ignoreCase = true)
                    || keyword.contains(other = book.contents, ignoreCase = true)
                    || keyword.contains(other = book.authors.joinToString(), ignoreCase = true)
                    || keyword.contains(other = book.publisher, ignoreCase = true)
                    || keyword.contains(other = book.translators.joinToString(), ignoreCase = true)
        }
        val sorted = when (sort) {
            GetBookmarkedBooksQuery.Sort.TITLE_ASCENDING -> filtered.sortedBy { it.title }
            GetBookmarkedBooksQuery.Sort.TITLE_DESCENDING ->
                filtered.sortedByDescending { it.title }
        }
        val ranged = sorted.filter { book -> book.effectivePrice in priceRange }

        return flowOf(value = Books(data = ranged))
    }

    override suspend fun clearBookmarkedBooks() {
    }

    override suspend fun addBookmark(book: Book) {
        bookmarkedBooks.add(book)
    }

    override suspend fun removeBookmark(book: Book) {
        bookmarkedBooks.remove(book)
    }

    override suspend fun isBookmark(book: Book): Boolean = bookmarkedBooks.contains(book)

    private fun fakeBook(): List<Book> = listOf(
        Book(
            title = "한강 스페셜 에디션",
            contents = "2024년 10월, “역사적 트라우마를 정면으로 마주하고 인간 삶의 연약함을 드러내는 강렬하고 시적인 산문”이라는 선정 이유와 함께 한국 최초 노벨문학상 수상 작가로 호명된 한강. 아시아 여성으로서는 최초 수상이며 역대 열여덟번째 여성 작가의 노벨문학상 수상이라는 점 또한 새로운 의미가 되었다. 한강 작가의 빛나는 성취를 기쁘게 축하하며 그의 30년 작품세계의 주요 마디가 되는 세 권의 소설을 특별한 장정으로 펴낸다. 흰 무명천에 수놓인 작품 제목을",
            url = "https://search.daum.net/search?w=bookpage&bookId=6789372&q=%ED%95%9C%EA%B0%95+%EC%8A%A4%ED%8E%98%EC%85%9C+%EC%97%90%EB%94%94%EC%85%98",
            isbn = "1141601591 9791141601591",
            datetime = simpleDateFormat.parse("2024-12-10T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 46800,
            salePrice = 42120,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6789372%3Ftimestamp%3D20250705145144",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "소년이 온다",
            contents = "2014년 만해문학상, 2017년 이탈리아 말라파르테 문학상을 수상하고 전세계 20여개국에 번역 출간되며 세계를 사로잡은 우리 시대의 소설 『소년이 온다』. 이 작품은 『채식주의자』로 인터내셔널 부커상을 수상한 한강 작가에게 “눈을 뗄 수 없는, 보편적이며 깊은 울림”(뉴욕타임즈), “역사와 인간의 본질을 다룬 충격적이고 도발적인 소설”(가디언), “한강을 뛰어넘은 한강의 소설”(문학평론가 신형철)이라는 찬사를 선사한 작품으로, 그간 많은 독자들",
            url = "https://search.daum.net/search?w=bookpage&bookId=532683&q=%EC%86%8C%EB%85%84%EC%9D%B4+%EC%98%A8%EB%8B%A4",
            isbn = "8936434128 9788936434120",
            datetime = simpleDateFormat.parse("2014-05-19T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "창비",
            translators = emptyList(),
            price = 15000,
            salePrice = 13500,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F532683%3Ftimestamp%3D20251018110418",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "디 에센셜: 한강(무선 보급판)",
            contents = "일으킨 작가를 선정한다. 작가의 작품세계를 고루 조망해 수록작을 선정하고 표지와 편집을 새로이 한 ‘디 에센셜 한국작가 편’을 한국문학에 입문하는 첫 책으로, 혹은 한국작가를 재발견하는 기회로 두루 누려주시길 바란다. 첫번째 작가는 한강이다. 한강 작가는 1993년 등단 후 30년간 문학이 삶에 제기하는 근본적인 물음─인간은 어떻게 서로를 믿고 사랑하는가, 세상은 왜 이토록 아름다우며 동시에 잔인한가, 상실과 고통 앞에 인간은 무엇을 할 수 있나─을 정면으로",
            url = "https://search.daum.net/search?w=bookpage&bookId=6360275&q=%EB%94%94+%EC%97%90%EC%84%BC%EC%85%9C%3A+%ED%95%9C%EA%B0%95%28%EB%AC%B4%EC%84%A0+%EB%B3%B4%EA%B8%89%ED%8C%90%29",
            isbn = "8954693466 9788954693462",
            datetime = simpleDateFormat.parse("2023-06-01T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 17000,
            salePrice = 15300,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6360275%3Ftimestamp%3D20250108152900",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "여수의 사랑",
            contents = "1993년 등단 이후 단단하고 섬세한 문장으로 삶의 근원에 자리한 고독과 아픔을 살펴온 한강이 지금까지 출간한 소설집을 새로운 옷을 갈아입혀 독자들 앞에 새롭게 선보인다. 1995년에 출간된 한강의 첫 책이자 첫 번째 소설집 『여수의 사랑』. 삶의 본질적인 외로움과 고단함을 섬세하게 살피며 존재의 상실과 방황을 그려낸다. 소설 배치를 바꾸고 몇몇 표현을 다듬어 선보이는 일곱 편의 단편들에서 운명과 죽음에 대한 저자의 진지한 시선을 엿볼 수 있다",
            url = "https://search.daum.net/search?w=bookpage&bookId=4368611&q=%EC%97%AC%EC%88%98%EC%9D%98+%EC%82%AC%EB%9E%91",
            isbn = "8932034818 9788932034812",
            datetime = simpleDateFormat.parse("2018-11-09T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학과지성사",
            translators = emptyList(),
            price = 16000,
            salePrice = 14400,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F4368611%3Ftimestamp%3D20241210125930",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "채식주의자",
            contents = "2016년 인터내셔널 부커상을 수상하며 한국문학의 입지를 한단계 확장시킨 한강의 장편소설 『채식주의자』를 15년 만에 새로운 장정으로 선보인다. 상처받은 영혼의 고통과 식물적 상상력의 강렬한 결합을 정교한 구성과 흡인력 있는 문체로 보여주는 이 작품은 섬뜩한 아름다움의 미학을 한강만의 방식으로 완성한 역작이다. “탄탄하고 정교하며 충격적인 작품으로, 독자들의 마음에 그리고 아마도 그들의 꿈에 오래도록 머물 것이다”라는 평을 받으며 인터내셔널 부커상을",
            url = "https://search.daum.net/search?w=bookpage&bookId=6042324&q=%EC%B1%84%EC%8B%9D%EC%A3%BC%EC%9D%98%EC%9E%90",
            isbn = "8936434594 9788936434595",
            datetime = simpleDateFormat.parse("2022-03-28T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "창비",
            translators = emptyList(),
            price = 17000,
            salePrice = 15300,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6042324%3Ftimestamp%3D20251017142248",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "빛과 실",
            contents = "“역사적 트라우마를 정면으로 마주하고 인간 삶의 연약함을 드러내는 강렬하고 시적인 산문”이라는 선정 이유와 함께 2024년 노벨문학상을 수상한 작가 한강의 신작 『빛과 실』(2025)이 문학과지성사 산문 시리즈 〈문지 에크리〉의 아홉번째 책으로 출간되었다. 노벨문학상 수상 강연문 「빛과 실」(2024)을 포함해 미발표 시와 산문, 그리고 작가가 자신의 온전한 최초의 집으로 ‘북향 방’과 ‘정원’을 얻고서 써낸 일기까지 총 열두 꼭지의 글이, 역시 작가",
            url = "https://search.daum.net/search?w=bookpage&bookId=6897292&q=%EB%B9%9B%EA%B3%BC+%EC%8B%A4",
            isbn = "8932043566 9788932043562",
            datetime = simpleDateFormat.parse("2025-04-18T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학과지성사",
            translators = emptyList(),
            price = 15000,
            salePrice = 13500,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6897292%3Ftimestamp%3D20250710144332",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "흰",
            contents = "“고독과 고요, 그리고 용기. 이 책이 나에게 숨처럼 불어넣어준 것은 그것들이었다.”",
            url = "https://search.daum.net/search?w=bookpage&bookId=6873595&q=%ED%9D%B0",
            isbn = "1141601710 9791141601713",
            datetime = simpleDateFormat.parse("2025-03-31T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 14500,
            salePrice = 13050,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6873595%3Ftimestamp%3D20250703143042",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "작별하지 않는다",
            contents = "2016년 『채식주의자』로 인터내셔널 부커상을 수상하고 2018년 『흰』으로 같은 상 최종 후보에 오른 한강 작가의 5년 만의 신작 장편소설 『작별하지 않는다』가 출간되었다. 2019년 겨울부터 이듬해 봄까지 계간 『문학동네』에 전반부를 연재하면서부터 큰 관심을 모았고, 그뒤 일 년여에 걸쳐 후반부를 집필하고 또 전체를 공들여 다듬는 지난한 과정을 거쳐 완성되었다. 본래 「눈 한 송이가 녹는 동안」(2015년 황순원문학상 수상작), 「작별」(2018년",
            url = "https://search.daum.net/search?w=bookpage&bookId=5824679&q=%EC%9E%91%EB%B3%84%ED%95%98%EC%A7%80+%EC%95%8A%EB%8A%94%EB%8B%A4",
            isbn = "8954682154 9788954682152",
            datetime = simpleDateFormat.parse("2021-09-09T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 16800,
            salePrice = 15120,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5824679%3Ftimestamp%3D20241210151048",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "서랍에 저녁을 넣어 두었다",
            contents = "한강 문학의 시적 기원! “한강의 소설에 등장하는 수많은 그림의 실재가 궁금했던 사람들은 이제 시집 『서랍에 저녁을 넣어 두었다』를 펼치면 된다”  1993년 계간 『문학과사회』 겨울호에 시 「서울의 겨울」 외 4편을 발표하고 이듬해 『서울신문』 신춘문예에 단편소설 「붉은 닻」이 당선되어 작품 활동을 시작한 한강이 틈틈이 쓰고 발표한 시들 중 60편을 추려 묶어 데뷔 20년 만에 펴낸 첫 시집이다. 인간 삶의 고독과 비애, 삶과 죽음의 경계에서 맞닥뜨리는",
            url = "https://search.daum.net/search?w=bookpage&bookId=495222&q=%EC%84%9C%EB%9E%8D%EC%97%90+%EC%A0%80%EB%85%81%EC%9D%84+%EB%84%A3%EC%96%B4+%EB%91%90%EC%97%88%EB%8B%A4",
            isbn = "8932024634 9788932024639",
            datetime = simpleDateFormat.parse("2013-11-15T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학과지성사",
            translators = emptyList(),
            price = 12000,
            salePrice = 10800,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F495222%3Ftimestamp%3D20250108114121",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "흰",
            contents = "한국인 최초 맨부커상 수상 작가 한강의 소설 『흰』. 2018년 맨부커 인터내셔널 부문 최종후보작으로 선정된 이 작품은, 2013년 겨울에 기획해 2014년에 완성된 초고를 바탕으로 글의 매무새를 닳도록 만지고 또 어루만져서 2016년 5월에 처음 펴냈던 책이다. 삶과 죽음이라는 경계를 무력하게 만드는 이 소설은 한 권의 시집으로 읽힘에 손색이 없는 65편의 이야기로 구성되어 있다.  강보, 배내옷, 각설탕, 입김, 달, 쌀, 파도, 백지, 백발, 수의",
            url = "https://search.daum.net/search?w=bookpage&bookId=694134&q=%ED%9D%B0",
            isbn = "8954651135 9788954651134",
            datetime = simpleDateFormat.parse("2018-04-25T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 14500,
            salePrice = 13050,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F694134%3Ftimestamp%3D20250415112819",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "희랍어 시간",
            contents = "한국인 최초 맨부커상 수상 작가 한강의 장편소설 『희랍어 시간』. 말을 잃어가는 한 여자의 침묵과 눈을 잃어가는 한 남자의 빛이 만나는 순간을 그리고 있다. 열일곱 살 겨울, 여자는 어떤 원인이나 전조 없이 말을 잃는다. 말을 잃고 살던 그녀의 입을 다시 움직이게 한 건 낯선 외국어였던 한 개의 불어 단어였다. 시간이 흘러, 이혼을 하고 아이의 양육권을 빼앗기고 다시 말을 잃어버린 여자는 죽은 언어가 된 희랍어를 선택한다. 그곳에서 만난 희랍어 강사",
            url = "https://search.daum.net/search?w=bookpage&bookId=690963&q=%ED%9D%AC%EB%9E%8D%EC%96%B4+%EC%8B%9C%EA%B0%84",
            isbn = "8954616518 9788954616515",
            datetime = simpleDateFormat.parse("2011-11-10T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 13000,
            salePrice = 11700,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F690963%3Ftimestamp%3D20250328113738",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "검은 사슴",
            contents = "정전을 완성하고자 구성한 「문학동네 한국문학전집」의 스물네 번째 작품은 세련되고 충격적인 이미지, 우아하고 힘 있는 묘사, 그것들을 하나로 꿰는 견고한 서사를 바탕으로 등단 이후 줄곧 문단과 독자들에게 강렬한 독서 체험을 선사해준 한강의 첫 장편소설이다.    1993년 등단 후 꼬박 3년간 집필에 몰두해 완성한 이 작품은 치밀하고 빈틈없는 서사와 깊은 울림을 주는 시적인 문장들로 출간 당시 찬사를 받았다. 작품의 제목이기도 한 ‘검은 사슴’은 깊은 땅속, 좁다란",
            url = "https://search.daum.net/search?w=bookpage&bookId=693865&q=%EA%B2%80%EC%9D%80+%EC%82%AC%EC%8A%B4",
            isbn = "8954648908 9788954648905",
            datetime = simpleDateFormat.parse("2017-12-20T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 15500,
            salePrice = 13950,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F693865%3Ftimestamp%3D20250918110352",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "바람이 분다 가라",
            contents = "한국인 최초 맨부커상 수상 작가 한강의 네 번째 장편소설 『바람이 분다, 가라』. 나직하면서도 힘 있는 문장과 시정 어린 문체로 인간의 본질적인 욕망과 삶의 진실을 탐문해온 작가 한강이 삶과 죽음의 경계 위에서 간절하게 숨 쉬는 사람들의 이야기를 들려준다.  촉망 받던 한 여자 화가의 죽음을 둘러싼 의문을 중심으로, 각자가 믿는 진실을 증명하기 위해 온몸으로 부딪치고 상처 입는 사람들의 이야기를 그리고 있다. 새벽의 미시령 고개에서 40년이란 시간의",
            url = "https://search.daum.net/search?w=bookpage&bookId=494274&q=%EB%B0%94%EB%9E%8C%EC%9D%B4+%EB%B6%84%EB%8B%A4+%EA%B0%80%EB%9D%BC",
            isbn = "8932020000 9788932020006",
            datetime = simpleDateFormat.parse("2010-02-26T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학과지성사",
            translators = emptyList(),
            price = 14000,
            salePrice = 12600,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F494274%3Ftimestamp%3D20250702153846",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "노랑무늬영원",
            contents = "1993년 등단 이후 단단하고 섬세한 문장으로 삶의 근원에 자리한 고독과 아픔을 살펴온 한강이 지금까지 출간한 소설집을 새로운 옷을 갈아입혀 독자들 앞에 새롭게 선보인다. 2002년 여름부터 일곱 달에 걸쳐 쓴 중편 《노랑무늬영원》을 비롯해 12년 동안 쓰고 발표한 일곱 편의 작품을 묶은 세 번째 소설집 『노랑무늬영원』. 《채식주의자》, 《바람이 분다, 가라》 등의 장편들과 긴밀하게 연결되고 조응하는 중편과 단편들의 자취가 고스란히 담겼다.",
            url = "https://search.daum.net/search?w=bookpage&bookId=4368529&q=%EB%85%B8%EB%9E%91%EB%AC%B4%EB%8A%AC%EC%98%81%EC%9B%90",
            isbn = "8932034834 9788932034836",
            datetime = simpleDateFormat.parse("2018-11-09T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학과지성사",
            translators = emptyList(),
            price = 14000,
            salePrice = 12600,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F4368529%3Ftimestamp%3D20241210125929",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "내 여자의 열매",
            contents = "1993년 등단 이후 단단하고 섬세한 문장으로 삶의 근원에 자리한 고독과 아픔을 살펴온 한강이 지금까지 출간한 소설집을 새로운 옷을 갈아입혀 독자들 앞에 새롭게 선보인다. 첫 소설집 《여수의 사랑》 이후 5년 만에 출간한 두 번째 소설집 『내 여자의 열매』는 《채식주의자》 연작의 씨앗이 된 《내 여자의 열매》 등을 포함한 단편 여덟 편의 배치를 바꾸고 표현과 문장을 다듬어 18년 만에 독자들과 다시 만난다. 작은 박새처럼 쉽게 파괴될 수 있는 연약한",
            url = "https://search.daum.net/search?w=bookpage&bookId=4368497&q=%EB%82%B4+%EC%97%AC%EC%9E%90%EC%9D%98+%EC%97%B4%EB%A7%A4",
            isbn = "8932034826 9788932034829",
            datetime = simpleDateFormat.parse("2018-11-09T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학과지성사",
            translators = emptyList(),
            price = 14000,
            salePrice = 12600,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F4368497%3Ftimestamp%3D20241210125928",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "그대의 차가운 손",
            contents = "안쪽을 파고드는 뜨거운 응시 껍데기 이면에 숨죽인 쓸쓸한 진실에 관하여  1993년 계간 『문학과사회』겨울호에 시 「서울의 겨울」 외 4편을 발표하고 이듬해 『서울신문』신춘문예에 단편소설 「붉은 닻」이 당선되어 작품 활동을 시작한 한강이 『검은 사슴』(1998) 이후 4년 만에 펴낸 두번째 장편소설이다. 이 책에서 작가는 미술 조각 기법의 일종인 ‘라이프캐스팅(석고 등의 소재를 이용해 인체를 그대로 본뜨는 방식)’이라는 장치를 통해 실존의 고통과 상처를",
            url = "https://search.daum.net/search?w=bookpage&bookId=496934&q=%EA%B7%B8%EB%8C%80%EC%9D%98+%EC%B0%A8%EA%B0%80%EC%9A%B4+%EC%86%90",
            isbn = "8932013047 9788932013046",
            datetime = simpleDateFormat.parse("2002-01-18T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학과지성사",
            translators = emptyList(),
            price = 14000,
            salePrice = 12600,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F496934%3Ftimestamp%3D20241210113404",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강(아이세움 지식그림책 30)(양장본 HardCover)",
            contents = "아이들의 성장에 도움을 줄 지식을 안겨주는 「아이세움 지식그림책」 제30권 『한강』. 500km에 달하는 한강을 지도 따라 굽이굽이 살펴보면서 역사 여행을 떠나는 지식그림책이다. 선사 시대부터 현대 사회까지 힘차게 흘러가는 한강을 구석구석 둘러보면서 교과서에서도 배우지 못한 우리나라 역사는 물론, 문화와 지리를 지도 따라 생생하게 체험해본다. 한강의 이야기를 풍부하게 살리는 관련 사진과 그림을 알차게 곁들였다. 뒷부분에는 <하루 만에 한강 돌아보기",
            url = "https://search.daum.net/search?w=bookpage&bookId=546301&q=%ED%95%9C%EA%B0%95%28%EC%95%84%EC%9D%B4%EC%84%B8%EC%9B%80+%EC%A7%80%EC%8B%9D%EA%B7%B8%EB%A6%BC%EC%B1%85+30%29%28%EC%96%91%EC%9E%A5%EB%B3%B8+HardCover%29",
            isbn = "8937846640 9788937846649",
            datetime = simpleDateFormat.parse("2011-10-10T00:00:00.000+09:00"),
            authors = listOf("김하늘"),
            publisher = "미래엔아이세움",
            translators = emptyList(),
            price = 13000,
            salePrice = 11700,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F546301%3Ftimestamp%3D20220630125114",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강: 회복하는 인간(Convalescence)",
            contents = "한국 대표 작가들의 작품을 영어로 번역하여, 한글과 영어로 동시에 읽을 수 있는 「바이링궐 에디션 한국 현대 소설」 시리즈 제 24권 『한강: 회복하는 인간』. 이번 세트2는 자유, 사랑과 연애, 남과 북이라는 카테고리로 나뉘어져 있다. 한국 현대사에서 익숙한 문제의식이지만 젊은 세대나 외국 독자들의 이해를 돕고자 카테고리에 대한 간소한 설명과 작가들의 작품에 대한 짧지만 심도 있는 해설과 작가 소개를 수록하였다.    한국인 최초 맨부커상 수상작가",
            url = "https://search.daum.net/search?w=bookpage&bookId=1412016&q=%ED%95%9C%EA%B0%95%3A+%ED%9A%8C%EB%B3%B5%ED%95%98%EB%8A%94+%EC%9D%B8%EA%B0%84%28Convalescence%29",
            isbn = "8994006826 9788994006826",
            datetime = simpleDateFormat.parse("2013-06-15T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "아시아",
            translators = listOf("전승희"),
            price = 9500,
            salePrice = 8550,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1412016%3Ftimestamp%3D20241210113349",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강(개정판 2판)(신나는 교과연계 체험학습 40)",
            contents = "현직 초등학교 교사 1000명이 추천하는 「신나는 교과연계 체험학습」 제40권 『한강』. 이 시리즈는 체험학습을 가기 전에, 체험학습 현장에서, 체험학습을 다녀와서 유용하게 활용하도록 구성되어 있다. 자율적 학습 능력을 길러 주는 체험학습을 통해 논술 실력까지 향상하게 된다. 이 책은 대한민국 수도 서울의 젖줄인 한강으로 아이들을 안내하고 있다. 오늘날 서울이 세계적 정치, 경제, 사회, 문화의 중심지가 되도록 힘을 발휘한 한강의 역사를 배워나간다. 한강",
            url = "https://search.daum.net/search?w=bookpage&bookId=520512&q=%ED%95%9C%EA%B0%95%28%EA%B0%9C%EC%A0%95%ED%8C%90+2%ED%8C%90%29%28%EC%8B%A0%EB%82%98%EB%8A%94+%EA%B5%90%EA%B3%BC%EC%97%B0%EA%B3%84+%EC%B2%B4%ED%97%98%ED%95%99%EC%8A%B5+40%29",
            isbn = "8934957409 9788934957409",
            datetime = simpleDateFormat.parse("2013-03-26T00:00:00.000+09:00"),
            authors = listOf("윤태호"),
            publisher = "주니어김영사",
            translators = emptyList(),
            price = 8000,
            salePrice = 7200,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F520512%3Ftimestamp%3D20220227175716",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강(아름다운 우리 땅 우리 문화 7)(양장본 HardCover)",
            contents = "『한강』은 오랜 옛날부터 맑고 밝게 뻗어 내린 강이라 열수라 부르고, 크고 신성한 강이라 아리수라 불렸던 한강의 굽이굽이에 흐르는 생태, 문화, 역사 이야기를 운율 넘치는 글과 섬세한 그림으로 담아낸 그림책이다. 강이 전하는 위대하고 감동적인 이야기를 만나 볼 수 있다.",
            url = "https://search.daum.net/search?w=bookpage&bookId=1626126&q=%ED%95%9C%EA%B0%95%28%EC%95%84%EB%A6%84%EB%8B%A4%EC%9A%B4+%EC%9A%B0%EB%A6%AC+%EB%95%85+%EC%9A%B0%EB%A6%AC+%EB%AC%B8%ED%99%94+7%29%28%EC%96%91%EC%9E%A5%EB%B3%B8+HardCover%29",
            isbn = "1186075457 9791186075456",
            datetime = simpleDateFormat.parse("2016-01-05T00:00:00.000+09:00"),
            authors = listOf("한정아"),
            publisher = "파란자전거",
            translators = emptyList(),
            price = 10900,
            salePrice = 9810,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1626126%3Ftimestamp%3D20200214134242",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강 세트(전10권)",
            contents = "작가정신의 승리라고 불릴 만큼 자신의 일생을 문학에 온전히 바쳐온 소설가 조정래의 『한강 세트』 전10권. 1970년 문예지 '현대문학'을 통해 문단에 나온 후 왜곡된 민족사에서 개인이 처한 한계에 이르기까지 다양한 영역을 아우르며 창작 활동을 펼쳐온 저자의 대하소설 3부작 중 그가 스스로 필생의 업이라고 표현한 《한강》을 읽는다. 민족적 삶의 진정한 모습을 전체적으로 구현하고자 하는 의욕을 바탕으로, 1959년 이후 격동의 현대사 30년간 한반도의",
            url = "https://search.daum.net/search?w=bookpage&bookId=1021388&q=%ED%95%9C%EA%B0%95+%EC%84%B8%ED%8A%B8%28%EC%A0%8410%EA%B6%8C%29",
            isbn = "8973378309 9788973378302",
            datetime = simpleDateFormat.parse("2008-08-28T00:00:00.000+09:00"),
            authors = listOf("조정래"),
            publisher = "해냄출판사",
            translators = emptyList(),
            price = 138000,
            salePrice = 124200,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1021388%3Ftimestamp%3D20221025121719",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "눈물상자",
            contents = "한국인 최초 맨부커상 수상 작가 한강이 선보이는 어른을 위한 동화『눈물상자』. 1994년 서울신문 신춘문예에 단편소설 '붉은 닻'이 당선되며 작품활동을 시작한 작가 한강은 한국소설문학상, 오늘의 젊은 예술가상, 이상문학상을 수상하기도 했다. 이 짧은 동화는 눈물은 투명하지만, 그것들을 결정으로 만들면 각기 다른 색깔이 나올 거란 생각을 바탕으로 하고 있다.    옛날, 아주 오랜 옛날은 아닌 옛날. 어느 마을에 보통의 사람들이 이해할 수 없는 일에",
            url = "https://search.daum.net/search?w=bookpage&bookId=689972&q=%EB%88%88%EB%AC%BC%EC%83%81%EC%9E%90",
            isbn = "8954605818 9788954605816",
            datetime = simpleDateFormat.parse("2008-05-22T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 10000,
            salePrice = 9000,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F689972%3Ftimestamp%3D20241210113343",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "작별하지 않는다 : 사인본(교보문고 랜선 팬 사인회 전용 상품)",
            contents = "2016년 『채식주의자』로 인터내셔널 부커상을 수상하고 2018년 『흰』으로 같은 상 최종 후보에 오른 한강 작가의 5년 만의 신작 장편소설 『작별하지 않는다』가 출간되었다. 2019년 겨울부터 이듬해 봄까지 계간 『문학동네』에 전반부를 연재하면서부터 큰 관심을 모았고, 그뒤 일 년여에 걸쳐 후반부를 집필하고 또 전체를 공들여 다듬는 지난한 과정을 거쳐 완성되었다. 본래 「눈 한 송이가 녹는 동안」(2015년 황순원문학상 수상작), 「작별」(2018년",
            url = "https://search.daum.net/search?w=bookpage&bookId=5840821&q=%EC%9E%91%EB%B3%84%ED%95%98%EC%A7%80+%EC%95%8A%EB%8A%94%EB%8B%A4+%3A+%EC%82%AC%EC%9D%B8%EB%B3%B8%28%EA%B5%90%EB%B3%B4%EB%AC%B8%EA%B3%A0+%EB%9E%9C%EC%84%A0+%ED%8C%AC+%EC%82%AC%EC%9D%B8%ED%9A%8C+%EC%A0%84%EC%9A%A9+%EC%83%81%ED%92%88%29",
            isbn = " 2090000107449",
            datetime = simpleDateFormat.parse("2021-09-09T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 14000,
            salePrice = 12600,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5840821%3Ftimestamp%3D20250812141551",
            status = "예약판매",
            isBookmarked = true
        ),
        Book(
            title = "한강",
            contents = "<한강>은 과거부터 서울에 사는 사람들의 삶에서 없어서는 안 되는 곳인 한강에 얽힌 가슴 아픈 이야기를 담고 있습니다.",
            url = "https://search.daum.net/search?w=bookpage&bookId=6933010&q=%ED%95%9C%EA%B0%95",
            isbn = " 9791134221195",
            datetime = simpleDateFormat.parse("2025-06-04T00:00:00.000+09:00"),
            authors = listOf("이루미"),
            publisher = "이루미에듀테크",
            translators = emptyList(),
            price = 6000,
            salePrice = -1,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6933010%3Ftimestamp%3D20250606150558",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "악스트(Axt)(2022년 1/2월호 40호) : 한강",
            contents = "리라는 그의 말이 더 큰 울림으로 다가온다. 2022년, 당신도 문학 속에서 반추하고 또 나아가기를. 그리고 그 자리에 『Axt』를 함께 놓아주기를 바란다.  신년호 cover story 인터뷰이는 ‘매번 사력을 다하는’ 소설가 한강이다. 생과 역사를 둘러싼 단단하고 차가운 어둠 속에서도 한 줌의 온기를 가진 문자들을 길어내는 일을, 그 사력을 다하는 일을 담당해온 그의 고요하고 청아한 목소리가 지면에 담겼다. 근작 『작별하지 않는다』를 ‘죽음에서 삶으로",
            url = "https://search.daum.net/search?w=bookpage&bookId=6244587&q=%EC%95%85%EC%8A%A4%ED%8A%B8%28Axt%29%282022%EB%85%84+1%2F2%EC%9B%94%ED%98%B8+40%ED%98%B8%29+%3A+%ED%95%9C%EA%B0%95",
            isbn = "9772384367000 9772384367000",
            datetime = simpleDateFormat.parse("2022-12-16T00:00:00.000+09:00"),
            authors = listOf("은행나무 편집부"),
            publisher = "은행나무",
            translators = emptyList(),
            price = 10000,
            salePrice = 9000,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6244587%3Ftimestamp%3D20250531161937",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강 다리, 서울을 잇다",
            contents = "대한민국 수도권에 거주하는 사람이라면 으레 한강에 대한 개인적인 추억이 하나씩은 있을 것이다. 2025년 현재 수도권에 한국의 인구 50% 이상이 거주하고 있다는 점을 생각하면 한국인들에게 있어 한강이라는 존재가 미치는 영향력이 어느 정도 될지 어렴풋이나마 짐작할 수 있다. 오늘날만의 이야기는 아니다. 으레 역사 시간에 배우듯이 한반도에서 한강 유역이 가지는 중요성은 몹시 큰 것이었고, 수많은 드라마가 한강을 둘러싸고 전개되어왔다. 길이 500km",
            url = "https://search.daum.net/search?w=bookpage&bookId=6842441&q=%ED%95%9C%EA%B0%95+%EB%8B%A4%EB%A6%AC%2C+%EC%84%9C%EC%9A%B8%EC%9D%84+%EC%9E%87%EB%8B%A4",
            isbn = "8962626454 9788962626452",
            datetime = simpleDateFormat.parse("2025-02-14T00:00:00.000+09:00"),
            authors = listOf("윤세윤"),
            publisher = "동아시아",
            translators = emptyList(),
            price = 20000,
            salePrice = 18000,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6842441%3Ftimestamp%3D20250419155029",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "디 에센셜: 한강",
            contents = "디 에센셜The essential 한국작가 편 첫번째 작가는 한강이다. 한강 작가는 1993년 등단 후 30년 가까이 문학이 삶에 제기하는 근본적인 물음─인간은 어떻게 서로를 믿고 사랑하는가, 세상은 왜 이토록 아름다우며 동시에 잔인한가, 상실과 고통 앞에 인간은 무엇을 할 수 있나─을 정면으로 마주한 작품을 다양한 장르로 써왔다.  소설과 시뿐만 아니라 어른을 위한 동화나 자신이 직접 만들고 부른 노래와 글을 함께 담은 산문집, 시와 소설이 어우러진 작품",
            url = "https://search.daum.net/search?w=bookpage&bookId=6082782&q=%EB%94%94+%EC%97%90%EC%84%BC%EC%85%9C%3A+%ED%95%9C%EA%B0%95",
            isbn = "8954686893 9788954686891",
            datetime = simpleDateFormat.parse("2022-05-30T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 17000,
            salePrice = 15300,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6082782%3Ftimestamp%3D20241109153216",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강",
            contents = "교과서에 나오는 바로 그 장소, 그 내용!  〈신나는 교과연계 체험학습〉과 함께 준비된 체험학습을 떠나자!  대한민국의 수도 서울의 젖줄, 한강!    예로부터 강은 인류의 삶과 깊은 관계를 맺어 왔다. 인류의 4대 문명이 모두 강 유역에서 그 뿌리를 둔 사실만 봐도 알 수 있다. 한강도 우리나라 역사에서 매우 중요한 역할을 했다. 삼국 시대부터, 아니 그 이전에도 한강 유역을 서로 차지하려고 치열한 다툼을 벌여왔다. 한강을 차지하는 세력은 늘 우리",
            url = "https://search.daum.net/search?w=bookpage&bookId=5340380&q=%ED%95%9C%EA%B0%95",
            isbn = "8934993057 9788934993056",
            datetime = simpleDateFormat.parse("2020-04-02T00:00:00.000+09:00"),
            authors = listOf("윤태호"),
            publisher = "주니어김영사",
            translators = emptyList(),
            price = 8500,
            salePrice = 7650,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5340380%3Ftimestamp%3D20250314143215",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강",
            contents = "서시.  한강을 건너  태양이 물기를 말려 땅은 드디어 물에서 해방을 얻고 거친 숨을 몰아쉬며,  한쪽 세상을 향하여 돌아보지 않더라.그리하여 그 쪽 물은 얼대로 얼어서 해빙의 순서를 기다리기까지 하도 많은 세월이 흘러,  둘이는 그리움을 원망으로 원망을 한탄으로 밤 가는 줄 모르고 서러운 울음을 울어 그 눈물은 드디어 강을 이루고, 그리하여 세월은 흘러서 강을 이루니,  그 강을 일러 한강이라 하더라.    -한강1.  사랑하는 마루하님.     우리 서럽도록",
            url = "https://search.daum.net/search?w=bookpage&bookId=4383866&q=%ED%95%9C%EA%B0%95",
            isbn = " 480D140600820",
            datetime = simpleDateFormat.parse("2014-06-21T00:00:00.000+09:00"),
            authors = listOf("김석현"),
            publisher = "논밭",
            translators = emptyList(),
            price = 1000,
            salePrice = -1,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F4383866%3Ftimestamp%3D20190228133235",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강",
            contents = "한강』은 권오학 시인의 첫 시조집으로, 굵직한 주제와 깊은 사유가 녹아있는 작품들로 구성되어 있다. 이 작품집은 한국의 민족정신과 역사적 현실을 다루며, 시인의 철학과 사회 참여에 대한 믿음과 가치관이 선명하게 드러나는데, 특히 역사적 분단과 정치적 현실에 대한 시인의 감정과 관심이 두드러진다. 『한강』은 이러한 철학적 토대를 가진 시조집으로서, 진리의 아름다움을 추구한다.  시인의 서문에서는 “다언多言은 병病이고 번문煩文은 욕이라 했고, 문장文章",
            url = "https://search.daum.net/search?w=bookpage&bookId=6503559&q=%ED%95%9C%EA%B0%95",
            isbn = "1191201554 9791191201550",
            datetime = simpleDateFormat.parse("2023-10-30T00:00:00.000+09:00"),
            authors = listOf("권오학"),
            publisher = "열린출판",
            translators = emptyList(),
            price = 20000,
            salePrice = 18000,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F6503559%3Ftimestamp%3D20241019155606",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강",
            contents = "이상엽 시집『한강』. 이상엽 시인의 시적 대상을 인간과 자연을 모티프로 삼고 문학 작품으로 형상화 시켰다. 총 5부로 나누어 '바위 가슴', '한 줌 흙이 되어', '부서지는 하루', '왜 눈물이 난다', '두 줄의 수레바퀴 자국', '내 발자국 소리', '용두사미', '아파트 동과 동 사이', '계절의 자락', '소나무와 진달래', '개미 한 마리' 등의 작품이 수록되어 있다.",
            url = "https://search.daum.net/search?w=bookpage&bookId=1107705&q=%ED%95%9C%EA%B0%95",
            isbn = "8979545401 9788979545401",
            datetime = simpleDateFormat.parse("2013-07-25T00:00:00.000+09:00"),
            authors = listOf("이상엽"),
            publisher = "천우",
            translators = emptyList(),
            price = 7000,
            salePrice = 6300,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1107705%3Ftimestamp%3D20241026114002",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강",
            contents = "배성호, 박형근, 그리고 서울당산초등학교 아이들의 『서울 교과서 한강』. 한강의 역사와 문화, 그리고 생태환경 등에 대한 지식을 이야기 형식으로 재미있게 담아냈다. 서울당산초등학교 아이들이 직접 참여했다는 것이 특징이다. 아이들의 이해를 도와주면서 공감을 안겨준다. 특히 한강을 친근하게 여기고 보호할 수 있도록 인도하고 있다. 채원경의 그림이 흥미를 불러일으킨다.",
            url = "https://search.daum.net/search?w=bookpage&bookId=1415941&q=%ED%95%9C%EA%B0%95",
            isbn = "8993912114 9788993912111",
            datetime = simpleDateFormat.parse("2009-12-23T00:00:00.000+09:00"),
            authors = listOf("서울 당산초등학교 어린이들", "배성호", "박형근"),
            publisher = "청어람주니어",
            translators = emptyList(),
            price = 11000,
            salePrice = 9900,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1415941%3Ftimestamp%3D20250625113735",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강",
            contents = "김원 시집 [한강]. 자신만의 작품세계를 구축해온 저자의 시 작품을 감상할 수 있다. 이번 시집에서는 한강 연작시를 수록했다.",
            url = "https://search.daum.net/search?w=bookpage&bookId=5104769&q=%ED%95%9C%EA%B0%95",
            isbn = "8965751217 9788965751212",
            datetime = simpleDateFormat.parse("2019-10-10T00:00:00.000+09:00"),
            authors = listOf("김원"),
            publisher = "엠애드",
            translators = emptyList(),
            price = 10000,
            salePrice = 9000,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5104769%3Ftimestamp%3D20230425165559",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강",
            contents = "박정래 시집. ［우리글시선］28번째인 이번 시집에서 시인은 '한강'을 모티브로 한 시를 총 5부로 나누어 선보이고 있다.",
            url = "https://search.daum.net/search?w=bookpage&bookId=1297801&q=%ED%95%9C%EA%B0%95",
            isbn = "8989376564 9788989376569",
            datetime = simpleDateFormat.parse("2007-01-31T00:00:00.000+09:00"),
            authors = listOf("박정래"),
            publisher = "우리글",
            translators = emptyList(),
            price = 6000,
            salePrice = 5400,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1297801%3Ftimestamp%3D20230905150604",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강",
            contents = "",
            url = "https://search.daum.net/search?w=bookpage&bookId=122983&q=%ED%95%9C%EA%B0%95",
            isbn = " 2003352001037",
            datetime = simpleDateFormat.parse("1998-03-16T00:00:00.000+09:00"),
            authors = listOf("서울특별시 편집부"),
            publisher = "서울특별시",
            translators = emptyList(),
            price = 5000,
            salePrice = -1,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F122983%3Ftimestamp%3D20250501134121",
            status = "",
            isBookmarked = true
        ),
        Book(
            title = "한강",
            contents = "",
            url = "https://search.daum.net/search?w=bookpage&bookId=194340&q=%ED%95%9C%EA%B0%95",
            isbn = " 2005888001390",
            datetime = simpleDateFormat.parse("1986-09-01T00:00:00.000+09:00"),
            authors = listOf("김춘배"),
            publisher = "제삼기획",
            translators = emptyList(),
            price = 2500,
            salePrice = -1,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F194340%3Ftimestamp%3D20250501143209",
            status = "",
            isBookmarked = true
        ),
        Book(
            title = "한강(엽서)",
            contents = "",
            url = "https://search.daum.net/search?w=bookpage&bookId=284784&q=%ED%95%9C%EA%B0%95%28%EC%97%BD%EC%84%9C%29",
            isbn = " 2011546000209",
            datetime = simpleDateFormat.parse("2000-06-01T00:00:00.000+09:00"),
            authors = listOf("연두와파랑 편집부"),
            publisher = "연두와파랑",
            translators = emptyList(),
            price = 2500,
            salePrice = -1,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F284784%3Ftimestamp%3D20250501153712",
            status = "",
            isBookmarked = true
        ),
        Book(
            title = "한강(1-6)",
            contents = "유신과 부마항쟁, 광주민주화운동 등 파란만장한 격동의 현대사를 본격적으로 다룬 장편 소설. 월북한 아버지를 둔 일민과 같은 고향 출신의 전형적인 출세주의자 강기수 의원은 4.19 학생혁명과 군사 쿠데타가 일어나자 특유의 처세술과 기회주의적 능력으로 군부의 끈을 잡는 데 성공한다. 일민과 교우하던 선배 이규백과 김선오는 강 의원이 운영하는 남천장학사에 기거하면서 고시에 합격하고, 강의원은 그 둘 중 한 사람을 사위로 삼고 싶어하지만, 딸 숙자의 저항",
            url = "https://search.daum.net/search?w=bookpage&bookId=253830&q=%ED%95%9C%EA%B0%95%281-6%29",
            isbn = " 2008092001347",
            datetime = simpleDateFormat.parse("2001-12-01T00:00:00.000+09:00"),
            authors = listOf("조정래"),
            publisher = "해냄출판사",
            translators = emptyList(),
            price = 48000,
            salePrice = -1,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F253830%3Ftimestamp%3D20250501151740",
            status = "",
            isBookmarked = true
        ),
        Book(
            title = "한강(전10권)",
            contents = "",
            url = "https://search.daum.net/search?w=bookpage&bookId=1019500&q=%ED%95%9C%EA%B0%95%28%EC%A0%8410%EA%B6%8C%29",
            isbn = "897337575X 9788973375752",
            datetime = simpleDateFormat.parse("2003-08-19T00:00:00.000+09:00"),
            authors = listOf("조정래"),
            publisher = "해냄출판사",
            translators = emptyList(),
            price = 98000,
            salePrice = 135000,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1019500%3Ftimestamp%3D20220930204140",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강(MYSTIC RIVER)(반양장)",
            contents = "",
            url = "https://search.daum.net/search?w=bookpage&bookId=1445651&q=%ED%95%9C%EA%B0%95%28MYSTIC+RIVER%29%28%EB%B0%98%EC%96%91%EC%9E%A5%29",
            isbn = "8995537183 9788995537183",
            datetime = simpleDateFormat.parse("2007-11-21T00:00:00.000+09:00"),
            authors = listOf("조동준"),
            publisher = "진디지털닷컴",
            translators = emptyList(),
            price = 35000,
            salePrice = -1,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1445651%3Ftimestamp%3D20221025133434",
            status = "",
            isBookmarked = true
        ),
        Book(
            title = "한강(빛깔있는 책들 99)",
            contents = "",
            url = "https://search.daum.net/search?w=bookpage&bookId=538845&q=%ED%95%9C%EA%B0%95%28%EB%B9%9B%EA%B9%94%EC%9E%88%EB%8A%94+%EC%B1%85%EB%93%A4+99%29",
            isbn = "8936900994 9788936900991",
            datetime = simpleDateFormat.parse("2003-07-01T00:00:00.000+09:00"),
            authors = listOf("이형석", "김주환"),
            publisher = "대원사",
            translators = emptyList(),
            price = 13000,
            salePrice = 11700,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F538845%3Ftimestamp%3D20220927171521",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강(고려원시문학총서 13)",
            contents = "",
            url = "https://search.daum.net/search?w=bookpage&bookId=15901&q=%ED%95%9C%EA%B0%95%28%EA%B3%A0%EB%A0%A4%EC%9B%90%EC%8B%9C%EB%AC%B8%ED%95%99%EC%B4%9D%EC%84%9C+13%29",
            isbn = " 2000370014759",
            datetime = simpleDateFormat.parse("1985-08-01T00:00:00.000+09:00"),
            authors = listOf("이근배"),
            publisher = "고려원",
            translators = emptyList(),
            price = 4000,
            salePrice = -1,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F15901%3Ftimestamp%3D20250501121655",
            status = "",
            isBookmarked = true
        ),
        Book(
            title = "흰",
            contents = "한국인 최초 맨부커상 수상 작가 한강의 신작 소설 『흰』. 2013년 겨울에 기획해 2014년에 완성된 초고를 바탕으로 글의 매무새를 닳도록 만지고 또 어루만져서 2016년 5월인 오늘에 이르러 펴낸 책이다. 삶과 죽음이라는 경계를 무력하게 만드는 이 소설은 한 권의 시집으로 읽힘에 손색이 없는 65편의 이야기로 구성되어 있다.    강보, 배내옷, 각설탕, 입김, 달, 쌀, 파도, 백지, 백발, 수의…. 작가로부터 불려나온 흰 것의 목록은 총 65",
            url = "https://search.daum.net/search?w=bookpage&bookId=692550&q=%ED%9D%B0",
            isbn = "8954640710 9788954640718",
            datetime = simpleDateFormat.parse("2016-06-01T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "난다",
            translators = emptyList(),
            price = 11500,
            salePrice = -1,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F692550%3Ftimestamp%3D20241022114552",
            status = "",
            isBookmarked = true
        ),
        Book(
            title = "한강(신나는 교과연계 체험학습 47)",
            contents = "",
            url = "https://search.daum.net/search?w=bookpage&bookId=519132&q=%ED%95%9C%EA%B0%95%28%EC%8B%A0%EB%82%98%EB%8A%94+%EA%B5%90%EA%B3%BC%EC%97%B0%EA%B3%84+%EC%B2%B4%ED%97%98%ED%95%99%EC%8A%B5+47%29",
            isbn = "8934931620 9788934931621",
            datetime = simpleDateFormat.parse("2010-04-26T00:00:00.000+09:00"),
            authors = listOf("윤태호"),
            publisher = "주니어김영사",
            translators = emptyList(),
            price = 7000,
            salePrice = -1,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F519132%3Ftimestamp%3D20220227175841",
            status = "",
            isBookmarked = true
        ),
        Book(
            title = "한강 1(양장본 HardCover)",
            contents = "",
            url = "https://search.daum.net/search?w=bookpage&bookId=1020148&q=%ED%95%9C%EA%B0%95+1%28%EC%96%91%EC%9E%A5%EB%B3%B8+HardCover%29",
            isbn = "8973375652 9788973375653",
            datetime = simpleDateFormat.parse("2003-08-05T00:00:00.000+09:00"),
            authors = listOf("조정래"),
            publisher = "해냄출판사",
            translators = emptyList(),
            price = 15000,
            salePrice = 13500,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1020148%3Ftimestamp%3D20221025121703",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "미국주식으로 한강뷰 가기",
            contents = "쉽게 다가갈 수 있는 한국주식과 달리 어렵게만 느껴지는 미국주식. 한강뷰가 알려주는 미국주식 필수 기초 개념들과 함께라면 전혀 그렇지 않다.  미국주식의 장단점부터 정보를 수집할 수 있는 다양한 수단, 기업의 실적과 재무제표를 보는 방법, 더 나아가 꼭 알아야 하는 기초 개념들까지 초보 투자자가 반드시 알아야 하는 내용을 모두 담은 책.  한강뷰에 가는 순간을 꿈꾸며 시작하는 미국주식. 이 한 권으로 시작합시다.",
            url = "https://search.daum.net/search?w=bookpage&bookId=5822590&q=%EB%AF%B8%EA%B5%AD%EC%A3%BC%EC%8B%9D%EC%9C%BC%EB%A1%9C+%ED%95%9C%EA%B0%95%EB%B7%B0+%EA%B0%80%EA%B8%B0",
            isbn = "1165454858 9791165454852",
            datetime = simpleDateFormat.parse("2021-09-01T00:00:00.000+09:00"),
            authors = listOf("한강뷰(윤현상)"),
            publisher = "바른북스",
            translators = emptyList(),
            price = 18000,
            salePrice = 16200,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5822590%3Ftimestamp%3D20220905152828",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "내 이름은 태양꽃",
            contents = "한국인 최초 맨부커상 수상작가 한강의 어른을 위한 동화 『내 이름을 태양꽃』. 어둡고 습한 담장 밑에서 어린 싹이 머리를 내미는 것으로 이야기는 시작된다. 땅속에서 나오기만 하면 환한 빛이 가득할 줄 알았던 어린 싹의 눈에 비친 세상은 온통 어두운 빛깔뿐. 담쟁이는 부지런히 자라나서 담장을 넘어가버리고 어린 싹은 자신이 담장을 넘을 수 없는 처지라는 것을 알고 고개만 수그릴 뿐인데... 보잘것없는 풀 한 포기가 태양보다 밝고 빛보다 환한 꽃으로 성장",
            url = "https://search.daum.net/search?w=bookpage&bookId=1160727&q=%EB%82%B4+%EC%9D%B4%EB%A6%84%EC%9D%80+%ED%83%9C%EC%96%91%EA%BD%83",
            isbn = "8982814795 9788982814792",
            datetime = simpleDateFormat.parse("2002-03-11T00:00:00.000+09:00"),
            authors = listOf("한강"),
            publisher = "문학동네",
            translators = emptyList(),
            price = 8500,
            salePrice = 7650,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F1160727%3Ftimestamp%3D20250409113816",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강(보름달 밤의 긴 내 말 제 1집 제 11권)",
            contents = "흘러간 내 시간의 발자국이 남겨놓은 여적(餘滴)을 모아본다. 하나 둘, 둘 하나, 아쉬움, 다 붙들지 못해 깨어진 체로, 흩어져 버린 파편들에 대한 연민(憐憫)이 남아 다시 그리움으로 어딘가를 가야한다는 무엇인가를 생각해야 한다는 당위 사실 가치 명제들이 지친 심신을 끌어당겨 한 매듭, 두 매듭, 매듭을 지어보았다.  그대로 두어버림이 차라리 나을 일인지도 모를 일지만, 그리운 그 그리움의 낡은 이미지로, 남아버림이 더 고고하고 순수하기도 하련만",
            url = "https://search.daum.net/search?w=bookpage&bookId=4753056&q=%ED%95%9C%EA%B0%95%28%EB%B3%B4%EB%A6%84%EB%8B%AC+%EB%B0%A4%EC%9D%98+%EA%B8%B4+%EB%82%B4+%EB%A7%90+%EC%A0%9C+1%EC%A7%91+%EC%A0%9C+11%EA%B6%8C%29",
            isbn = " 4809050184939",
            datetime = simpleDateFormat.parse("2013-06-17T00:00:00.000+09:00"),
            authors = listOf("김석현"),
            publisher = "논밭",
            translators = emptyList(),
            price = 1000,
            salePrice = -1,
            thumbnail = "",
            status = "",
            isBookmarked = true
        ),
        Book(
            title = "한강문학(2022 신년호)",
            contents = "《한강문학》은 ‘전문 문학지’입니다. 편집방향은 ‘역사, 전통, 문화, 예술’입니다. 문학은 그 모든 것을 아우르는 방편입니다. 일독을 권합니다.",
            url = "https://search.daum.net/search?w=bookpage&bookId=5411959&q=%ED%95%9C%EA%B0%95%EB%AC%B8%ED%95%99%282022+%EC%8B%A0%EB%85%84%ED%98%B8%29",
            isbn = "9772383695005 9772383695005",
            datetime = simpleDateFormat.parse("2022-02-28T00:00:00.000+09:00"),
            authors = listOf("한강문학회"),
            publisher = "한강문학",
            translators = emptyList(),
            price = 15000,
            salePrice = 14250,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5411959%3Ftimestamp%3D20250531152134",
            status = "정상판매",
            isBookmarked = true
        ),
        Book(
            title = "한강문학(2021 가을호)",
            contents = "《한강문학》은 ‘전문 문학지’입니다. 편집방향은 ‘역사, 전통, 문화, 예술’입니다. 문학은 그 모든 것을 아우르는 방편입니다. 일독을 권합니다.",
            url = "https://search.daum.net/search?w=bookpage&bookId=5487266&q=%ED%95%9C%EA%B0%95%EB%AC%B8%ED%95%99%282021+%EA%B0%80%EC%9D%84%ED%98%B8%29",
            isbn = "9772383695005 9772383695005",
            datetime = simpleDateFormat.parse("2021-10-26T00:00:00.000+09:00"),
            authors = listOf("한강문학회"),
            publisher = "한강문학",
            translators = emptyList(),
            price = 15000,
            salePrice = 14250,
            thumbnail = "https://search1.kakaocdn.net/thumb/R120x174.q85/?fname=http%3A%2F%2Ft1.daumcdn.net%2Flbook%2Fimage%2F5487266%3Ftimestamp%3D20250531153030",
            status = "정상판매",
            isBookmarked = true
        )
    )
}
