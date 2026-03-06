# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# 빌드
./gradlew build

# 테스트 실행 (JaCoCo 커버리지 리포트 포함)
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "baekgwa.blogserver.domain.post.service.PostServiceTest"

# 특정 테스트 메서드 실행
./gradlew test --tests "baekgwa.blogserver.domain.post.service.PostServiceTest.methodName"

# QueryDSL Q클래스 생성
./gradlew compileJava

# 빌드 정리 (Q클래스 포함)
./gradlew clean
```

## JaCoCo 커버리지 기준

- Branch coverage: 70% 이상
- Line coverage: 80% 이상
- 제외 패키지: `global`, `infra`, `BlogServerApplication`, QueryDSL Q클래스, `domain.ai`

## 테스트 환경

테스트는 `@ActiveProfiles("test")` 를 사용하며:
- DB: H2 인메모리 (MySQL 모드), Flyway 비활성화
- Redis: localhost:6379 (실제 Redis 연결 필요)
- 외부 서비스 Mock: `S3Client`, `FileUploader`, `StreamingChatModel`, `EmbeddingService`, `ViewCountStore`, `RestClient`

모든 통합 테스트는 `SpringBootTestSupporter`를 상속받아 작성한다.

## 프로젝트 구조

```
src/main/java/baekgwa/blogserver/
├── domain/          # 비즈니스 도메인 (controller / service / dto)
│   ├── ai/          # AI 스트리밍 채팅 (LangChain4J + OpenAI)
│   ├── authentication/  # JWT 로그인/로그아웃
│   ├── category/
│   ├── post/
│   ├── stack/       # 시리즈(스택) 기능
│   ├── tag/
│   └── upload/      # S3 파일 업로드
├── model/           # JPA 엔티티 & Repository
│   ├── category/
│   ├── embedding/   # 임베딩 실패 이력
│   ├── post/post/   # PostEntity
│   ├── post/tag/    # PostTagEntity (다대다 매핑)
│   └── stack/
├── infra/           # 인프라 관심사
│   ├── embedding/   # 임베딩 이벤트 처리 & 재시도 스케줄러
│   ├── upload/      # S3 업로더 구현체
│   └── view/        # 조회수 Redis 누적 → DB 배치 동기화
└── global/          # 공통 설정
    ├── cache/       # Redis 캐시 설정 (CacheType, CacheKeyFactory)
    ├── config/      # Spring 설정 (Security, JPA, Redis, OpenAI, Async 등)
    ├── environment/ # @ConfigurationProperties 바인딩 클래스
    ├── exception/   # GlobalException, GlobalExceptionHandler
    ├── filter/      # JWT 인증 필터
    ├── response/    # BaseResponse<T>, ErrorCode, SuccessCode
    └── util/        # JWTUtil, CookieUtil, SlugUtil
```

## 핵심 아키텍처 패턴

### 응답 형식
모든 API는 `BaseResponse<T>`로 응답한다. `SuccessCode`/`ErrorCode` enum을 사용해 코드와 메시지를 관리한다. 에러는 `GlobalException(ErrorCode)`를 throw하면 `GlobalExceptionHandler`가 처리한다.

### 캐싱 (Redis)
`CacheType` enum에 캐시 이름과 TTL(분)을 정의한다. `@Cacheable`/`@CacheEvict` 어노테이션을 사용하며, 캐시 키는 `CacheKeyFactory` 빈을 통해 SpEL로 생성한다.

### 임베딩 (비동기 이벤트)
포스트 생성/수정/삭제 시 `ApplicationEventPublisher`로 이벤트를 발행한다. `EmbeddingEventListener`가 `@Async` + `@TransactionalEventListener(AFTER_COMMIT)`으로 처리한다. 실패 시 `EmbeddingFailureEntity`에 저장하고, 스케줄러가 재시도한다.

### 조회수 처리
조회 이벤트 → `ViewCountRedisStore`에 Redis 누적 → 스케줄러(`ViewCountScheduler`)가 주기적으로 `ViewCountBatchService.synchronizeViewCounts()`를 호출해 DB에 bulk update한다.

### 인증
JWT 기반 Stateless 인증. `AuthenticationFilter`가 모든 요청에서 토큰을 검증한다. 인증이 필요없는 엔드포인트는 `SecurityConfig`에 명시되어 있다.

### QueryDSL
Q클래스는 `src/main/generated/`에 생성된다. `PostRepositoryCustom` 인터페이스를 구현해 복잡한 검색 쿼리를 처리한다.

## 프로파일 설정

- `dev`: MySQL + Redis + ElasticSearch (환경변수 필요)
- `prod`: 프로덕션 서비스
- `test`: H2 + 로컬 Redis, Flyway 비활성화

환경변수는 `.env` 파일로 주입 가능 (`optional:file:.env[.properties]`).

## 기술 스택

- Spring Boot 3.5.0, Java 21
- JPA + QueryDSL 5.1.0, MySQL (prod) / H2 (test), Flyway
- Redis (캐시 + 조회수), ElasticSearch (벡터 임베딩)
- LangChain4J 1.8.0 + OpenAI (AI 스트리밍)
- AWS S3 SDK 2.x (파일 업로드)
- Spring Security + JWT (jjwt 0.12.3)
- Actuator + Jolokia (JMX), ELK 스택 (Logstash 연동)

### 예외 처리

```java
// GlobalException + ErrorCode 패턴만 사용
throw new GlobalException(ErrorCode.NOT_FOUND);
```

- 직접 `RuntimeException` 등을 던지지 않는다
- 새로운 에러 케이스는 `ErrorCode`에 추가 후 사용
- 에러 케이스는 도메인별로 구분
    - 도메인 : 에러 코드 범위
    - Auth : 1000 ~ 1999
    - Category : 2000 : 2999
    - Tag : 3000 : 3999
    - Post : 4000 : 4999
    - Upload : 5000 ~ 5999
    - Stack : 6000 ~ 6999
    - Common: 9000 ~ 9999