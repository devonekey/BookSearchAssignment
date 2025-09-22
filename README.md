## 💁‍♂️ 프로젝트 개요

이 프로젝트는 kakao developers에서 제공하는 REST API를 활용하여 개발한 도서 검색 앱을 만드는 프로젝트입니다.

---

## 🎯 목적

'과제 테스트 전형'에서 요구하는 산출물을 제출하기 위한 용도로 만들었습니다.

---

## 🛠️ 핵심 기술

- 언어: Kotlin 2.2.20
- UI: XML 레이아웃
- 아키텍처: Clean Architecture
- 빌드: Gradle 8.13 (Kotlin DSL)
- 대상: Android API 28~36 (Android 9.0~15)
- JDK: OpenJDK 21.0.5

---

## 👷‍♂️ 빌드 방법

```bash
./gradlew assembleDebug    # 디버그 APK 빌드 (에뮬레이터/기기 테스트용)
./gradlew build            # 전체 빌드 (디버그 및 릴리스 변형 포함)
./gradlew clean            # 빌드 아티팩트 정리
```

---

## 🏗️ 프로젝트 구조

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

---
