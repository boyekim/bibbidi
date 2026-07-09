---
name: spring-craft
description: Java Spring Boot 백엔드를 컨벤션 기반으로 빠르게 생성, 확장, 리팩토링, 테스트하기 위한 스킬. Spring Boot/JPA 코드, API, 서비스, 엔티티, DTO, 검증, 예외, 테스트를 만들거나 기존 백엔드 코드베이스의 로컬 컨벤션을 보존하며 작업할 때 사용한다.
---

# Spring Craft

이 스킬은 작고 일관적이며 테스트 가능한 Spring Boot 백엔드 코드를 빠르게 만들기 위해 사용한다.

## 첫 단계

1. 파일을 수정하기 전 `git status --short` 를 실행한다. 사용자의 변경사항은 절대 되돌리지 않는다.
2. 코딩 전에 기존 패턴을 먼저 확인한다.
   - `rg --files src/main/java src/test/java`
   - 주변 API, 서비스, DTO, 엔티티, 리포지토리, 예외, 테스트를 검색한다.
3. 이 가이드와 프로젝트의 기존 스타일이 충돌하면 프로젝트의 기존 스타일을 우선한다.
4. 변경 범위는 작게 유지한다. 작업이 여러 파일이나 새 아키텍처로 커지면 계속 진행하기 전에 분리 지점을 보고한다.

## 기본 아키텍처

프로젝트에 더 강한 컨벤션이 없다면 다음 패키지 구조를 사용한다.

- `domain/<feature>/` — 도메인 모델, 리포지토리, 서비스, 사용자 API
- `domain/<feature>/dto/` — 요청/응답 record
- `admin/<feature>/` — 관리자 전용 API 또는 운영성 유스케이스
- `client/` — 외부 HTTP/API 어댑터와 properties
- `support/` — 공통 인프라, 예외, 웹 헬퍼, 스케줄러, 유틸리티
- `config/` — Spring 설정과 빈 정의

컨트롤러는 얇게, 서비스는 트랜잭션 경계로, 엔티티는 행위 중심으로, 인프라는 격리해서 작성한다.

## 구현 순서

새 기능은 보통 다음 순서로 만들거나 수정한다.

1. Entity 또는 도메인 객체
2. Repository
3. Request/Response DTO
4. Service
5. API/Controller
6. 필요한 경우 Exception code
7. 필요한 최소 테스트 또는 컴파일 검증

실제 중복을 줄이거나 로컬 패턴과 맞는 경우가 아니라면 새 추상화를 만들지 않는다.

## Entity 컨벤션

JPA 엔티티는 다음 형태를 기본으로 사용한다.

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Project(String name) {
        this.name = name;
    }

    public void rename(String name) {
        this.name = name;
    }
}
```

규칙:

- JPA를 위해 `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 를 사용한다.
- `@Getter` 를 선호하고 public setter는 피한다.
- 상태 변경은 의도가 드러나는 메서드에 둔다.
- enum은 `EnumType.STRING` 을 사용한다.
- 엔티티 생성자는 명시적으로 작성한다.
- 프로젝트가 이미 전역 `entity` 패키지를 쓰는 경우가 아니라면 엔티티는 기능 패키지 아래에 둔다.

## DTO 컨벤션

API DTO는 Java record를 사용한다.

네이밍:

- 요청 DTO: `*Request`
- 단일 응답 DTO: `*Response`
- 컬렉션 래퍼: 복수형 `*Responses`

응답 DTO는 필요하면 정적 매퍼를 제공한다.

```java
public record ProjectResponse(
    Long id,
    String name
) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(project.getId(), project.getName());
    }
}
```

컨트롤러에서 엔티티를 직접 노출하지 않는다.

## Service 컨벤션

클래스 레벨에는 read-only 트랜잭션을 기본으로 걸고, 쓰기 메서드에서만 재정의한다.

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectResponse create(ProjectCreateRequest request) {
        Project project = new Project(request.name());
        projectRepository.save(project);
        return ProjectResponse.from(project);
    }

    public ProjectResponse get(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new AppException(AppErrorCode.PROJECT_NOT_FOUND));
        return ProjectResponse.from(project);
    }
}
```

규칙:

- `@RequiredArgsConstructor` 로 생성자 주입을 사용한다.
- 조회 메서드는 read-only 상태를 유지한다.
- 쓰기 메서드는 `@Transactional` 을 사용한다.
- 검증과 권한 확인은 유스케이스에 가까운 곳에서 수행한다.
- 없는 리소스 조회는 `orElseThrow` 로 처리한다.
- 프로젝트가 이미 허용하는 구조가 아니라면 긴 DB 트랜잭션 안에서 외부 네트워크 호출을 하지 않는다.

## API 컨벤션

컨트롤러는 요청을 파싱하고 서비스에 위임하며 HTTP 응답 형태만 잡는다.

```java
@RestController
@RequiredArgsConstructor
public class ProjectApi {

    private final ProjectService projectService;

    @PostMapping("/api/projects")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        ProjectResponse response = projectService.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/projects/{projectId}")
    public ResponseEntity<ProjectResponse> get(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.get(projectId));
    }
}
```

규칙:

- HTTP 매핑은 명시적으로 작성한다.
- `ResponseEntity` 를 반환한다.
- Request DTO에 validation annotation을 사용하면 `@Valid` 를 함께 붙인다.
- 메서드 파라미터 검증을 사용하면 컨트롤러 클래스에 `@Validated` 가 있는지 확인한다.
- 비즈니스 로직을 컨트롤러에 넣지 않는다.

## Validation 규칙

검증 annotation은 트리거가 있을 때만 동작한다.

짝을 확인한다.

- DTO 필드의 `@NotBlank`, `@NotNull`, `@Size` 등은 컨트롤러 파라미터의 `@Valid` 가 필요하다.
- 메서드 파라미터의 `@Min`, `@Pattern`, `@NotBlank` 등은 컨트롤러 클래스의 `@Validated` 가 필요하다.
- Security annotation은 method security 활성화가 필요하다.
- Cache annotation은 caching 활성화가 필요하다.
- Async 또는 scheduled annotation은 해당 Spring 기능 활성화가 필요하다.

검증을 추가할 때는 한쪽만 바꾸지 말고 비슷한 request와 controller를 grep으로 먼저 확인한다.

## Exception 컨벤션

중앙화된 error code와 하나의 application exception 타입을 선호한다.

```java
public enum AppErrorCode {
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
```

```java
@Getter
public class AppException extends RuntimeException {

    private final AppErrorCode errorCode;

    public AppException(AppErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
    }
}
```

규칙:

- 애플리케이션 서비스에서 raw `IllegalArgumentException` 을 던지기보다 구체적인 error code를 추가한다.
- 도메인 예외는 타입과 메시지를 함께 테스트한다.
- 전역 예외 처리는 `@RestControllerAdvice` 로 중앙화한다.

## 테스트 전략

테스트는 최소한으로 작성한다. 새 기능마다 습관적으로 테스트 파일을 늘리지 말고, 변경 위험을 줄이는 데 실제로 필요한 케이스만 둔다.

기본 원칙:

- 먼저 기존 테스트가 있으면 그 테스트를 보강한다.
- 새 테스트는 핵심 성공 경로 1개와 중요한 실패/예외 경로 1개 정도를 기본 상한으로 삼는다.
- 단순 CRUD, 얇은 위임 컨트롤러, boilerplate DTO처럼 컴파일로 충분히 확인되는 변경에는 새 테스트를 만들지 않아도 된다.
- 버그 수정은 가능하면 실패를 재현하는 최소 테스트 1개만 추가한다.
- 넓은 통합 테스트는 API 계약, persistence 매핑, 트랜잭션, 보안처럼 깨졌을 때 영향이 큰 경우에만 사용한다.

테스트가 필요하다면 동작을 증명할 수 있는 가장 작은 테스트 타입을 선택한다.

- 순수 도메인 로직: plain unit test
- 리포지토리/DB 동작이 필요한 서비스: `@SpringBootTest`
- 컨트롤러 요청/응답 동작: `@WebMvcTest`
- 전체 HTTP 흐름: 정말 필요할 때만 integration test

네이밍:

- `*Test` — 로컬 컨벤션에 따라 단위 또는 서비스 테스트
- `*ApiTest` — controller/WebMvc 테스트
- `*IntegrationTest` — 넓은 통합 또는 전체 HTTP 흐름 테스트

AssertJ를 사용한다.

```java
assertThat(result).isEqualTo(expected);

assertThatThrownBy(() -> service.get(1L))
    .isInstanceOf(AppException.class)
    .hasMessageContaining("찾을 수 없습니다");
```

Given-When-Then 주석을 사용한다.

```java
// given

// when

// then
```

Spring Boot 3.4+의 WebMvc 테스트에서는 deprecated 된 `@MockBean` 대신 `@MockitoBean` 을 선호한다.

사용하지 말 것:

- 새 테스트에서 `Thread.sleep()` 사용. Awaitility 또는 결정적인 동기화 방식을 사용한다.
- 핵심 검증으로 `assertNotNull` 만 사용하는 것.
- 구현 결과를 expected에 복사하는 것.
- 테스트 대상 도메인 동작 자체를 mock으로 우회하는 것.

## Fixture 컨벤션

시끄러운 생성자를 테스트에 직접 늘어놓기보다 기존 fixture를 선호한다.

```java
import static com.example.fixture.ProjectFixture.*;

Project project = createProject("name");
```

새 fixture를 만들기 전에 기존 fixture 클래스와 시그니처를 확인한다. fixture는 재사용되거나 가독성을 유의미하게 높일 때만 추가한다. 한 테스트에서만 쓰는 간단한 객체라면 fixture를 새로 만들지 않는다.

## Bug Fix Workflow

수정 전:

1. 현재 동작을 확인한다.
2. 기대 동작을 확인한다.
3. 둘의 차이를 버그로 정의한다.
4. 가능하면 실패하는 재현 테스트를 최소 1개만 먼저 추가한다.

그 다음:

1. 같은 패턴이 다른 곳에도 있는지 grep으로 확인한다.
2. 가장 작은 책임 코드 경로를 수정한다.
3. compile과 관련 테스트만 실행한다.
4. 인접 문제를 조용히 함께 고치지 말고 별도 이슈로 보고한다.

## Refactoring Workflow

동작 보존 리팩토링에서는:

1. 수정 전 관련 테스트가 있으면 실행해 baseline을 확보한다.
2. 허용 범위와 금지 범위를 정의한다.
3. 명시 요청이 없다면 public behavior, message, log, API shape를 보존한다.
4. 리팩토링을 통과시키기 위해 테스트를 수정하지 않는다.
5. 수정 후 같은 테스트 또는 compile 검증을 다시 실행하고 baseline과 비교한다.

리팩토링 중 동작 버그를 발견하면 그대로 두고 별도 bug-fix 후보로 보고한다.

## Verification

프로젝트 native 명령을 사용한다. Gradle 예시:

```bash
./gradlew compileJava -q
./gradlew test --tests "com.example.ProjectServiceTest"
./gradlew clean build
```

작은 변경은 `compileJava` 또는 가장 가까운 focused test만 실행한다. 공통 인프라, API 계약, persistence 동작, 넓은 리팩토링처럼 위험이 큰 변경에서만 더 넓은 테스트 스위트를 실행한다.

## Final Report

완료 후 다음을 보고한다.

1. 변경 파일
2. 추가했거나 보존한 핵심 동작
3. 검증 명령과 결과
4. 현재 작업에서 분리한 인접 문제
5. 실제로 유용한 후속 작업
