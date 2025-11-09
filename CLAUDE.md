# CLAUDE.md

이 파일은 Claude Code에서 본 프로젝트를 작업할 때 참고하는 가이드입니다.

---

## 💁‍♂️ 프로젝트 개요

이 프로젝트는 kakao developers에서 제공하는 REST API를 활용하여 개발한 도서 검색 앱을 만드는 프로젝트입니다.

---

## 🛠️ 핵심 기술

- 언어: Kotlin 2.2.20
- UI: XML 레이아웃
- 아키텍처: Clean Architecture
- 빌드: Gradle 8.13 (Kotlin DSL)
- 대상: Android API 28~36 (Android 9.0~15)
- JDK: OpenJDK 21.0.5

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

---

## 🧪 테스트

### 테스트 실행

```bash
./gradlew test                   # 전체 테스트
```

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

---
