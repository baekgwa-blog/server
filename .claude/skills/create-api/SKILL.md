---
name: create-api
description: Controller, Service(Read/Write), Request DTO, Response DTO, Entity, Repository 생성 및 수정. API, 엔드포인트, 컨트롤러, 서비스, 파사드, 리포지토리, 엔티티, DTO, CRUD, 도메인, 기능 생성 언급 시 사용.
allowed-tools: Read, Write, Edit, Glob, Grep, Bash, LSP
---

# Spring API 개발 규칙

이 프로젝트의 Spring Boot REST API 개발 표준 규칙이다.

---

## 계층 구조 및 역할

```
Controller  →  Service  →  Repository
                  ↕
                Entity
```

| 계층 | 역할             | 규칙                             |
|------|----------------|--------------------------------|
| **Controller** | 요청 수신, 응답 반환   | 비즈니스 로직 없음         |
| **Service** | 서비스 로직 처리 | `매서드 마다 @Transactional 명시적 처리` |
| **Repository** | 데이터 접근         | JPA / QueryDSL                 |
| **Entity** | 도메인 모델         | 상태 변경은 내부 메서드로                 |

---

## 공통 규칙

- `@RequiredArgsConstructor` 로 생성자 주입 (필드 주입 `@Autowired` 금지)
- **객체 생성은 항상 static factory method 우선** (`of`, `from`, `create` 등)
- 빌더는 `@Builder(access = AccessLevel.PRIVATE)` 로 외부 직접 사용 금지
- `@Getter` 사용, **`@Setter` 절대 금지** → 상태 변경은 명시적 메서드로
- 불필요한 생성자 접근 차단: `@NoArgsConstructor(access = AccessLevel.PRIVATE or PROTECTED)`
    - Jackson 역직렬화(RequestBody)가 필요한 DTO: `PROTECTED`
    - 인스턴스화 자체를 막는 유틸/래퍼 클래스: `PRIVATE`

---

## Controller

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/{도메인}")
@Tag(name = "...", description = "...")
public class XxxController {

    @PostMapping("/new")
    @Operation(summary = "...")
    public BaseResponse<XxxResponse.XxxDto> create(
        @RequestBody @Valid XxxRequest.PostXxxDto dto
    ) {
        return BaseResponse.success(SuccessCode.XXX_SUCCESS, xxxService.create(dto));
    }

    // 파일 다운로드처럼 BaseResponse 사용 불가한 경우만 ResponseEntity<byte[]> 반환
    @GetMapping("/{id}/excel")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable Long id) {
        XxxResponse.ExcelDto response = xxService.getExcel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return ResponseEntity.ok().headers(headers).body(response.getExcelBytes());
    }
}
```

**규칙**
- `Service`만 의존한다. Repository 직접 호출 금지.
- 모든 응답은 `BaseResponse.success(SuccessCode, data)` 래퍼 사용
- `@Valid` 로 RequestBody 검증은 Controller에서 처리
- 쿼리 파라미터가 많으면 Controller에서 Request DTO로 조립 후 Service에 전달

## Service

```java
@Service
@RequiredArgsConstructor
public class XxxService {

    private final XxxRepository xxxRepository;

    /**
     * ID로 엔티티 조회, 없으면 예외
     */
    @Transactional(readOnly = true)
    public XxxEntity getXxxOrThrow(Long id) {
        return xxxRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_XXX));
    }

    /**
     * 페이징 목록 조회
     */
    @Transactional(readOnly = true)
    public PageResponse<XxxResponse.XxxInfo> getXxxList(XxxRequest.GetXxxInfo dto) {
        // 파라미터 유효성 검증
        if (dto.getPage() < 0 || dto.getSize() < 1) {
            throw new GlobalException(ErrorCode.INVALID_PAGINATION_PARAMETER);
        }
        Page<XxxResponse.XxxInfo> result = xxxRepository.searchXxxList(dto);
        return PageResponse.of(result);
    }

	/**
	 * 신규 Xxx 생성
	 */
	@Transactional
	public XxxEntity createXxxOrThrow(XxxRequest.PostXxxDto dto) {
		// 비즈니스 유효성 검증
		// ...

		XxxEntity entity = XxxEntity.createNew(/* 필요한 값 */);
		return xxxRepository.save(entity);
	}

	/**
	 * Xxx 수정
	 */
	@Transactional
	public void updateXxx(XxxEntity entity, /* 변경할 값 */) {
		entity.update(/* 변경할 값 */);
		// Dirty Checking으로 자동 반영 (별도 save 불필요)
	}
}
```

**규칙**
- 모든 메서드에 `@Transactional(readOnly = true)` or `@Transactional()`
- 조회 실패 시 `GlobalException` 으로 예외 처리
- 메서드명: `getXxxOrThrow`, `getXxxListOrThrow`, `findXxxMap` 등 의도 명확하게
- 단순 단건 저장은 Dirty Checking 활용, 명시적 `save()` 최소화
- 비즈니스 유효성 검증 (날짜 범위, 중복 여부 등)은 Service에서 처리
- Javadoc 으로 파라미터/반환값 설명 작성

---

## Request DTO

```java
@NoArgsConstructor(access = AccessLevel.PRIVATE)  // 래퍼 클래스 - 인스턴스화 방지
public class XxxRequest {

    // RequestBody로 받는 DTO
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // Jackson 역직렬화를 위해 PROTECTED
    public static class PostXxxDto {
        @NotNull(message = "xxx는 필수입니다.")
        private Long someId;

        @Min(value = 1L, message = "수량은 최소 1개입니다.")
        private Long quantity;
    }

    // 쿼리 파라미터 조합 DTO (Controller에서 직접 생성)
    @Getter
    public static class GetXxxInfo {
        final String keyword;
        final int page;
        final int size;

        public GetXxxInfo(String keyword, int page, int size) {
            this.keyword = keyword;
            this.page = page;
            this.size = size;
        }
    }
}
```

**규칙**
- 외부 클래스(`XxxRequest`)는 `@NoArgsConstructor(access = PRIVATE)` 로 인스턴스화 방지
- RequestBody DTO: Jackson이 역직렬화할 수 있도록 `@NoArgsConstructor(access = PROTECTED)`
- 쿼리파라미터 조합 DTO: 모든 필드 `final`, 생성자로만 생성
- `@Valid` 검증 어노테이션은 필드에 직접 선언

---

## Response DTO

```java
@NoArgsConstructor(access = AccessLevel.PRIVATE)  // 래퍼 클래스 - 인스턴스화 방지
public class XxxResponse {

    @Getter
    public static class XxxDetailInfo {
        private final Long id;
        private final String name;

        @Builder(access = AccessLevel.PRIVATE)
        private XxxDetailInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        // Entity → DTO 변환 static factory method
        public static XxxDetailInfo of(XxxEntity entity) {
            return XxxDetailInfo.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
        }
    }

    // 단순한 경우 빌더 없이 생성자만 사용
    @Getter
    public static class NewXxxDto {
        private final Long id;

        public NewXxxDto(Long id) {
            this.id = id;
        }
    }
}
```

**규칙**
- 외부 클래스(`XxxResponse`)는 `@NoArgsConstructor(access = PRIVATE)` 로 인스턴스화 방지
- Entity → DTO 변환은 `of(Entity)` 또는 `from(Entity)` static factory method
- 빌더는 `@Builder(access = AccessLevel.PRIVATE)`, 생성자는 `private`
- 단순한 구조는 빌더 없이 private 생성자 + static factory method

---

## Entity

```java
@Entity
@Getter
@Table(name = "xxx")
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // JPA 기본 생성자
public class XxxEntity extends TemporalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // 연관관계는 항상 LAZY
    @JoinColumn(name = "yyy_id", nullable = false)
    private YyyEntity yyyEntity;

    @Column(name = "name", nullable = false)
    private String name;

    @Builder(access = AccessLevel.PRIVATE)
    private XxxEntity(YyyEntity yyyEntity, String name) {
        this.yyyEntity = yyyEntity;
        this.name = name;
    }

    // 생성 static factory method
    public static XxxEntity createNew(YyyEntity yyyEntity, String name) {
        return XxxEntity.builder()
            .yyyEntity(yyyEntity)
            .name(name)
            .build();
    }

    // 상태 변경 메서드 (Setter 대신)
    public void updateName(String name) {
        this.name = name;
    }
}
```

**규칙**
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 필수
- `@Builder(access = AccessLevel.PRIVATE)` + `private` 생성자
- **반드시 static factory method** 로만 생성 (`createNew`, `createXxx`)
- 모든 연관관계는 `FetchType.LAZY`
- 상태 변경은 명시적 메서드로 (`update*`, `change*`, `register*`)

---

## Repository

```java
// 기본: JpaRepository만으로 충분한 경우
public interface XxxRepository extends JpaRepository<XxxEntity, Long> {
    List<XxxEntity> findByProject(ProjectEntity project);
}

// 동적 쿼리가 필요한 경우: Custom 인터페이스 + QueryDSL Impl 추가
public interface XxxRepository extends JpaRepository<XxxEntity, Long>, XxxRepositoryCustom {
}

public interface XxxRepositoryCustom {
    Page<XxxResponse.XxxInfo> searchXxxList(XxxRequest.GetXxxInfo dto);
}

@Repository
@RequiredArgsConstructor
public class XxxRepositoryImpl implements XxxRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private static final QXxxEntity xxx = QXxxEntity.xxxEntity;

    @Override
    public Page<XxxResponse.XxxInfo> searchXxxList(XxxRequest.GetXxxInfo dto) {
        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());
        BooleanBuilder where = createWhereCondition(dto);

        List<XxxResponse.XxxInfo> content = queryFactory
            .selectFrom(xxx)
            .where(where)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch()
            .stream()
            .map(XxxResponse.XxxInfo::of)
            .toList();

        Long total = queryFactory
            .select(xxx.count())
            .from(xxx)
            .where(where)
            .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanBuilder createWhereCondition(XxxRequest.GetXxxInfo dto) {
        BooleanBuilder builder = new BooleanBuilder();
        if (StringUtils.hasText(dto.getKeyword())) {
            builder.and(xxx.name.containsIgnoreCase(dto.getKeyword()));
        }
        return builder;
    }
}
```

**규칙**
- 단순 조회/저장: `JpaRepository` + Spring Data JPA 메서드명 규칙으로 충분
- 동적 쿼리(필터, 정렬, 페이징): `*RepositoryCustom` 인터페이스 + `*RepositoryImpl` (QueryDSL)
- QueryDSL Q클래스는 `static final` 필드로 선언
- where 조건, order 조건은 별도 private 메서드로 분리

---

## 예외 처리

```java
// 항상 GlobalException + ErrorCode 조합 사용
throw new GlobalException(ErrorCode.NOT_FOUND_XXX);
```

- `RuntimeException` 직접 사용 금지
- 새 에러는 `ErrorCode`에 도메인 범위에 맞게 추가 후 사용
- 에러 코드 범위는 CLAUDE.md 참고

---

## 트랜잭션 경계

| 위치 | 규칙 |
|------|------|
| Service 메서드 | `@Transactional` 필수 |
| Controller | 트랜잭션 없음 |
| 이벤트 리스너 | `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Async` |

---

## 페이지네이션

- 목록 조회는 항상 `Pageable` 기반
- 응답은 `PageResponse.of(page)` 래퍼 사용
- 파라미터 유효성(`page < 0`, `size < 1`) 은 Service에서 검증
