# CLAUDE.md

이 파일은 Claude Code에서 본 프로젝트를 작업할 때 참고하는 가이드입니다.

---

## 💁‍♂️ 프로젝트 개요

이 프로젝트는 kakao developers에서 제공하는 REST API를 활용하여 개발한 도서 검색 앱을 만드는 프로젝트입니다.

---

## 🛠️ 핵심 기술

- 언어: Kotlin 2.2.20 (Coroutine 및 Flow 활용)
- UI: XML 레이아웃
- 아키텍처: Clean Architecture (app, domain 모듈 분리)
- 빌드: Gradle 8.13 (Kotlin DSL)
- 대상: Android API 28~36 (Android 9.0~15)
- JDK: OpenJDK 21.0.5
- 테스트: JUnit 5, MockWebServer

---

## 👷‍♂️ 빌드 및 개발 명령어

### 빌드

```bash
./gradlew assembleDebug    # 디버그 APK 빌드 (에뮬레이터/기기 테스트용)
./gradlew build            # 전체 빌드 (디버그 및 릴리스 변형 포함)
./gradlew clean            # 빌드 아티팩트 정리
```

### 테스트

```bash
./gradlew test                       # 단위 테스트 실행 (전체)
./gradlew :domain:test               # domain 모듈 테스트
./gradlew connectedAndroidTest       # 기기/에뮬레이터 기반 테스트 실행
```

### Gradle Wrapper

- 프로젝트는 Gradle Wrapper (v8.13)를 사용
- `./gradlew` 또는 `./gradlew.bat`를 통해 실행
- 시스템 Gradle 대신 Wrapper 사용을 권장(빌드 일관성 보장)

---

## 🏗️ 프로젝트 구조

### 모듈 구성 (Clean Architecture)

```
BookSearchAssignment/
├── app/                    # 프레젠테이션 계층 (UI, Activity, ViewModel)
├── domain/                 # 도메인 계층 (비즈니스 로직, UseCase)
├── gradle/                 # Gradle 래퍼 및 설정
├── build.gradle.kts        # 루트 빌드 설정
├── settings.gradle.kts     # Gradle 설정
├── gradle.properties       # 속성
├── local.properties        # 로컬 속성
├── CLAUDE.md               # Claude Code 가이드
└── README.md               # 프로젝트 설명서
```

### app 모듈 (프레젠테이션 계층)

```
app/src/main/
├── kotlin/com/booksearch/assignment/
│   └── MainActivity.kt                 # 메인 액티비티
├── res/
│   ├── layout/                         # XML 레이아웃 파일
│   ├── drawable/                       # 이미지 리소스
│   ├── values/                         # 컬러, 문자열, 테마 정의
│   └── mipmap/                         # 앱 아이콘
└── AndroidManifest.xml                 # 앱 메타데이터
```

### domain 모듈 (도메인 계층)

```
domain/src/main/kotlin/com/booksearch/assignment/domain/
├── model/
│   ├── input/
│   │   ├── GetBookmarkedBooksQuery.kt  # 북마크된 도서들을 가져올 때 요구되는 질의
│   │   ├── SearchBooksQuery.kt         # 도서들을 검색할 때 요구되는 질의
│   │   └── ToggleBookmarkTarget.kt     # 도서를 북마크하거나 북마크를 제거하는 행위에 대한 입력
│   ├── output/
│   │   ├── BooksResult.kt              # 도서들을 검색하는 행위에 대한 결과
│   │   └── ToggleBookmarkResult.kt     # 도서를 북마크하거나 북마크를 제거하는 행위에 대한 출력
│   ├── Book.kt                         # 도서 모델
│   └── Books.kt                        # 여러 도서들을 다루는 모델
├── repository/
│   └── BookRepository.kt               # 도서 Repository 인터페이스 정의
├── usecase/
│   ├── GetBookmarkedBooksUseCase.kt    # 북마크된 도서들을 가져오는 유즈케이스
│   ├── ResetBookmarkedBooksUseCase.kt  # 가져온 북마크된 도서들을 초기화하고 다시 가져오는 유즈케이스
│   ├── ResetSearchedBooksUseCase.kt    # 검색된 도서들을 초기화하고 다시 검색하는 유즈케이스
│   ├── SearchBooksUseCase.kt           # 도서들을 검색하는 유즈케이스
│   ├── ToggleBookmarkUseCase.kt        # 도서를 북마크하거나 북마크를 제거하는 유즈케이스
│   └── UseCase.kt                      # 유즈케이스
└── util/
    └── EffectivePrice.kt               # 도서의 최종 판매가를 계산
```

### domain 모듈 테스트 코드

```
domain/src/test/kotlin/com/booksearch/assignment/domain/
├── model/
│   ├── BookTest.kt
│   └── BooksTest.kt
├── repository/
│   └── FakeBookRepositoryImpl.kt
├── usecase/
│   ├── GetBookmarkedBooksUseCaseTest.kt
│   ├── ResetBookmarkedBooksUseCaseTest.kt
│   ├── ResetSearchedBooksUseCaseTest.kt
│   ├── SearchBooksUseCaseTest.kt
│   └── ToggleBookmarkUseCaseTest.kt
└── util/
    └── EffectivePriceTest.kt
```

---

## 🧱 구성 파일

### 빌드 설정

- **build.gradle.kts** (루트): Android Gradle Plugin 8.12.3 설정
- **app/build.gradle.kts**: 앱 모듈 설정
- **gradle/libs.versions.toml**: 의존성 버전 일괄 관리

### 애플리케이션 설정

AndroidManifest.xml

- 앱 ID: `com.booksearch.assignment`
- Minimum SDK: 28
- Target SDK: 36
- Compile SDK: 36
- 테마: Material3 Day/Night
- Edge-to-Edge: 활성화

### gradle.properties

- JVM 인자: Gradle 빌드 성능 최적화
- Kotlin 코드 스타일: official
- Non-transitive R class 활성화

---

## 👨‍💼 아키텍처

### Clean Architecture 레이어

#### 1. 프레젠테이션 계층 (app 모듈)

- 책임: UI 표시 및 사용자 상호작용
- 구성: Activity, Fragment, ViewModel
- 원칙: 비즈니스 로직 포함 금지
- 현황: Material3 테마 적용, 기본 MainActivity

#### 2. 도메인 계층 (domain 모듈)

- 책임: 비즈니스 로직 및 규칙 정의
- 구성: UseCase, Repository 인터페이스, Entity
- 특징: Android/Framework 의존성 없이 순수 Kotlin으로만 작성
- 현황: 검색, 즐겨찾기 관련 UseCase 구현

---

## 🧪 테스트

### 테스트 구조

#### Domain 계층

- 대상: UseCase, Entity, 비즈니스 로직
- 방식: 외부 의존성 제거, 순수 로직 검증
- 도구: JUnit 5

### 테스트 실행

```bash
./gradlew test                   # 전체 테스트
./gradlew :domain:test           # domain 모듈만 테스트
```

### 테스트 작성 원칙

1. Given-When-Then 또는 Given-Expect 패턴 사용
2. 성공 케이스뿐 아니라 실패 케이스도 검증
3. 각 테스트는 독립적으로 실행 가능
4. Mock/Fake 객체로 외부 의존성 격리
5. 한 번에 하나의 기능만 검증

---

## 📖 개발 시 주의사항

### 1. API Key 관리

- API Key를 소스 코드에 직접 작성하지 않음
- `local.properties`에 저장 후 BuildConfig를 통해 주입함
- `.gitignore`에 `local.properties` 추가한 상태

### 2. 계층 혼용 금지

- 각 계층의 책임만 수행함
- 계층 간 인터페이스를 통하여 통신함

### 3. 테스트 의존성

- 실제 API를 호출하여 테스트하지 않음
- MockWebServer 또는 Fake 객체로 처리
- 로컬에서 빠른 테스트 실행 추구

### 4. 코루틴 사용

- 비동기 작업은 코루틴으로 구현
- suspend 함수로 순차적 실행
- Flow로 데이터 스트림 관리

---

## 📚 참고 자료

### 기본 공식 문서

- [Kotlin docs](https://kotlinlang.org/docs/home.html)
- [Android Developers](https://developer.android.com)

### 아키텍처

- [Clean Architecture - Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Architecture Components](https://developer.android.com/topic/architecture)

### 라이브러리

- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

### API 문서

- [kakao developers](https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-book)

---
