### 💁‍♂️ 소개
---
'카카오 도서 검색 API'를 활용해 '도서 검색 앱'을 개발한 프로젝트입니다.

### 🎯 목적
---
'과제 테스트 전형'에서 요구하는 산출물을 제출하기 위한 용도로 만들었습니다.

### 👷‍♂️ 빌드 방법
---
- 빌드 환경
  - Android Studio: Narwhal Feature Drop 2025.1.2 Patch 1 (Build #AI-251.26094.121.2512.13930704)
  - Gradle: 8.13
  - JDK: 21
  - Kotlin: 2.2.21
- 빌드 방법
  - [local.properties](local.properties) 파일에 REST API 키 입력 (ex. KAKAO_REST_API_KEY=**********)
  - CLI에서 "./gradlew assembleDebug" 입력

### 🛠️ 사용 프레임워크
---
- 언어: Kotlin(Coroutine, Flow 포함)
- 아키텍처: Clean Architecture, MVVM, TDD
- UI: Jetpack Compose, Material3
- 의존성 주입: Dagger Hilt
- 네트워크: Retrofit, OkHttp3, Gson
- 데이터베이스: Room
- 테스트: JUnit, MockWebServer

### 🏗️ 프로젝트 구조
---
- app/: Presentation Layer (UI, ViewModel, DI)
- data/: Data Layer (Repository, Local DB, Remote API, DI)
- domain/: Domain Layer (business logic, UseCase, Repository interface, DI)

### ⭐️ 주요 구현 포인트
---
기능이 완성될 때마다 테스트 코드를 추가하여, 기능의 신뢰성을 확보하도록 노력하였습니다.
