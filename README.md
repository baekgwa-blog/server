# Backgwa Blog - Server

개인 블로그 서비스의 백엔드 API 서버입니다.
JWT 인증, AI 스트리밍 채팅(RAG), 방문자 행동 기반 추천 시스템을 포함합니다.

---

## 아키텍처 개요

```
Client
  │
  ▼
[Server (이 서비스)]
  │  REST API
  ├─ JWT 인증 / 포스트 CRUD / 파일 업로드
  ├─ AI 스트리밍 채팅 (RAG + ElasticSearch)
  ├─ 조회수 (Redis → DB 배치 동기화)
  ├─ 방문자 세션 쿠키(sid) 발급
  │
  ├──XADD──▶ Redis Stream (post_embedding_events)
  ├──XADD──▶ Redis Stream (user_behavior_events)
  │                │
  │          [Pipeline 서비스]
  │           임베딩 생성 / 추천 계산
  │                │
  └──LRANGE/ZRANGE─▶ Redis (추천 결과 읽기)
```

포스트 생성/삭제 및 방문자 행동(조회, 검색)은 Redis Stream으로 이벤트를 발행합니다.
임베딩 생성과 추천 계산은 별도의 Pipeline 서비스가 소비하여 처리합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.0 |
| ORM | Spring Data JPA + QueryDSL 5.1.0 |
| Database | MySQL (prod) / H2 (test) |
| Migration | Flyway |
| Cache & Stream | Redis (캐싱, 조회수, Redis Stream) |
| Search | ElasticSearch (벡터 검색 / RAG) |
| AI | LangChain4J 1.8.0 + OpenAI |
| Auth | Spring Security + JWT (jjwt 0.12.3) |
| Storage | AWS S3 SDK 2.x |
| Monitoring | Spring Actuator + Jolokia (JMX), Logstash |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Test | JUnit 5, JaCoCo (Branch 70% / Line 80%) |

---

## 주요 기능

### 포스트
- 포스트 CRUD (카테고리, 태그, 썸네일 자동 추출)
- 키워드 / 카테고리 / 정렬 기반 목록 검색
- 조회수: Redis 누적 → 스케줄러 DB 배치 동기화 (중복 방지)
- 포스트 생성/삭제 시 Redis Stream으로 임베딩 이벤트 발행 → Pipeline 서비스 처리

### AI 채팅
- Server-Sent Events 기반 스트리밍 응답
- RAG: ElasticSearch Hybrid Search (BM25 + 벡터)로 관련 포스트 컨텍스트 주입
- Rate Limit 적용 (IP 기반)

### 추천
- 비로그인 방문자 세션 UUID 쿠키(`sid`) 자동 발급
- 포스트 조회 / 키워드 검색 시 행동 이벤트 발행 (Redis Stream)
- `GET /post/recommendation`: 세션 기반 개인화 추천 → 없으면 글로벌 트렌딩 폴백

### 인증
- JWT Stateless 인증 (`AuthenticationFilter`)
- 로그인 / 로그아웃 (쿠키 기반 토큰 관리)

### 파일 업로드
- AWS S3 presigned URL 방식

---

## 프로젝트 구조

```
src/main/java/baekgwa/blogserver/
├── domain/
│   ├── ai/           # AI 스트리밍 채팅 (LangChain4J)
│   ├── authentication/
│   ├── category/
│   ├── post/         # 포스트 CRUD, 행동 이벤트, 추천 API
│   ├── stack/        # 시리즈(스택) 기능
│   ├── tag/
│   └── upload/
├── model/            # JPA 엔티티 & Repository
├── infra/
│   ├── embedding/    # EmbeddingService (RAG 검색 전용)
│   ├── stream/       # RedisStreamPublisher, RedisStreamConfig, RedisStreamKeys
│   ├── upload/       # S3 업로더
│   └── view/         # 조회수 Redis 누적 → DB 배치
└── global/
    ├── cache/        # CacheType, CacheKeyFactory
    ├── config/       # Security, JPA, Redis, OpenAI, Async 등
    ├── filter/       # JWT 인증 필터
    ├── interceptor/  # SessionCookieInterceptor
    ├── response/     # BaseResponse, SuccessCode, ErrorCode
    └── util/         # JWTUtil, CookieUtil, SlugUtil
```

---

## Redis Stream 이벤트 계약

### `post_embedding_events`
포스트 생성/삭제 시 발행. Pipeline 서비스가 소비하여 ElasticSearch 임베딩을 동기화합니다.

```
eventType  : CREATE | DELETE
postId     : 포스트 ID
title      : 제목 (CREATE)
content    : HTML 원문 (CREATE)
description: 요약 (CREATE)
category   : 카테고리 이름 (CREATE)
tags       : 태그 이름 콤마 구분 (CREATE)
slug       : URL 슬러그 (CREATE)
occurredAt : 이벤트 발행 시각
```

### `user_behavior_events`
방문자 행동 이벤트 발행. Pipeline 서비스가 소비하여 추천 결과를 계산합니다.

```
eventType     : POST_VIEWED | POST_SEARCHED
sessionId     : 방문자 세션 UUID
postId        : 조회한 포스트 ID (POST_VIEWED)
slug          : 조회한 포스트 슬러그 (POST_VIEWED)
keyword       : 검색어 (POST_SEARCHED)
categoryFilter: 카테고리 필터 (POST_SEARCHED)
occurredAt    : 이벤트 발행 시각
```

---

## 실행 방법

### 필요 환경
- Java 21
- MySQL, Redis, ElasticSearch 실행 중
- `.env` 파일 또는 환경변수 설정

### 환경변수 예시 (`.env`)
```properties
MYSQL_URL=jdbc:mysql://localhost:3306/blog
MYSQL_USERNAME=root
MYSQL_PASSWORD=password
REDIS_HOST=localhost
REDIS_PORT=6379
ELASTICSEARCH_URIS=http://localhost:9200
OPENAI_API_KEY=sk-...
AWS_ACCESS_KEY=...
AWS_SECRET_KEY=...
AWS_S3_BUCKET=...
JWT_SECRET=...
```

### 빌드 및 실행
```bash
# 빌드
./gradlew build

# 테스트 (JaCoCo 커버리지 포함)
./gradlew test

# 실행 (dev 프로파일)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### API 문서
서버 실행 후 http://localhost:8080/swagger-ui/index.html

---

## 테스트

```bash
# 전체 테스트
./gradlew test

# 특정 클래스
./gradlew test --tests "baekgwa.blogserver.domain.post.service.PostServiceTest"
```

- 프로파일: `@ActiveProfiles("test")`
- DB: H2 인메모리 (MySQL 모드), Flyway 비활성화
- Redis: localhost:6379 실제 연결 필요
- 외부 서비스: `S3Client`, `StreamingChatModel`, `EmbeddingService`, `RedisStreamPublisher` Mock 처리
- 커버리지 기준: Branch 70% / Line 80% (domain 레이어)
