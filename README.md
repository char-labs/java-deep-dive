# Java Deep Dive

Java 25 + Spring Boot 4 환경에서 JVM/GC/동시성/OOP/람다 주제를 **실행 가능한 데모 + 측정 코드**로 묶은 프로젝트.

> 단순 API 만들기가 아니라 **개념을 손으로 확인**하는 게 목적입니다.

## 구조

```
java-cs-interview/
├── build.gradle.kts           # Spring Boot 4 + Java 25 toolchain + JMH
├── src/main/java/com/cs/javainterview/
│   ├── JavaCsInterviewApplication.java   # CLI 진입점
│   ├── common/                           # Demo 인터페이스 + Stopwatch
│   ├── jvm/                              # JIT 워밍업, ClassLoader
│   ├── gc/                               # Heap 구조, Minor GC, OOM
│   ├── concurrency/                      # 가시성/원자성/스레드풀
│   ├── oop/                              # 캡슐화/상속/다형성/인터페이스
│   └── stream/                           # 람다, lazy 평가
├── src/jmh/java/...                      # JMH 마이크로벤치마크
└── docs/
    ├── 01-jvm.md
    ├── 02-gc.md
    ├── 03-concurrency.md
    ├── 04-oop.md
    └── 05-lambda-stream.md
```

## 사전 요구

- JDK 25 (Gradle toolchain이 자동으로 다운로드 가능하지만, `JAVA_HOME`이 21+여야 wrapper가 부팅됨)
- Gradle 8.x (wrapper 사용 권장 — 별도 설치 시 `gradle wrapper`로 추가)

> 참고: Spring Boot 4.0과 Java 25 toolchain 조합은 GA 기준 사용 가능합니다. JVM 옵션이 뒤바뀌었거나 Spring Boot 패치 버전이 다르다면 `build.gradle.kts`의 버전 라인만 조정하세요.

## 실행

### 데모 목록 보기

```bash
./gradlew bootRun
```

### 개별 데모 실행

```bash
./gradlew bootRun --args='jvm.jit'
./gradlew bootRun --args='jvm.classloader'
./gradlew bootRun --args='gc.heap'
./gradlew bootRun --args='gc.minor'
./gradlew bootRun --args='gc.oom'                # 의도적 OOM (별도 JVM 옵션 권장)
./gradlew bootRun --args='concurrency.visibility'
./gradlew bootRun --args='concurrency.atomicity'
./gradlew bootRun --args='concurrency.threadpool'
./gradlew bootRun --args='oop.encapsulation'
./gradlew bootRun --args='oop.inheritance'
./gradlew bootRun --args='oop.polymorphism'
./gradlew bootRun --args='oop.interface-abstract'
./gradlew bootRun --args='stream.basic'
./gradlew bootRun --args='stream.lazy'
```

### JMH 벤치마크

```bash
./gradlew jmh -Pjmh.includes=InterpreterVsJitBenchmark
./gradlew jmh -Pjmh.includes=AllocationBenchmark
./gradlew jmh -Pjmh.includes=ParallelStreamBenchmark
```

### GC 로그를 보고 싶을 때

> ⚠️ `-Dorg.gradle.jvmargs`는 **Gradle 데몬** 자체의 JVM 옵션이라 여기에 GC/Heap 옵션을 넣으면 데몬이 죽습니다. **절대 사용 금지.**

`build.gradle.kts`의 `bootRun.jvmArgs` 안에 있는 GC 로그 라인 주석을 풀고 실행하세요.

```kotlin
// build.gradle.kts - 아래 주석 해제 후 ./gradlew bootRun --args='gc.minor'
jvmArgs = listOf(
    "--enable-preview",
    "-Xms256m", "-Xmx256m",
    "-Xlog:gc*:file=${layout.buildDirectory.get().asFile.absolutePath}/gc.log:time,uptime,level,tags",
    "-XX:+HeapDumpOnOutOfMemoryError",
    "-XX:HeapDumpPath=${layout.buildDirectory.get().asFile.absolutePath}/heapdump.hprof"
)
```

생성된 GC 로그 위치: `build/gc.log`

### OOM + 힙 덤프

위와 동일하게 `bootRun.jvmArgs` 안에서 `-Xmx128m`과 `HeapDumpOnOutOfMemoryError`를 활성화한 뒤:

```bash
./gradlew bootRun --args='gc.oom'
```

덤프 위치: `build/heapdump.hprof` → [Eclipse MAT](https://www.eclipse.org/mat/)로 분석

이후 [Eclipse MAT](https://www.eclipse.org/mat/)로 `build/heapdump.hprof`를 열어 Leak Suspects 분석.

각 문서는 **핵심 답변 → 꼬리질문 → 모범답변 확장 → 30초 요약** 구조입니다. 출퇴근/리뷰 직전 5분 컷으로 활용하세요.

## 데모 작성 원칙

- **모든 데모는 `Demo` 인터페이스 구현 + `@Component`** → Spring 컨텍스트가 자동 수집해서 CLI 인자로 선택 실행.
- 측정은 **JMH** (마이크로벤치) + **`Stopwatch`** (간단 측정) 두 가지를 주제에 맞게 사용.
- 출력은 표 형태 + "해석 가이드" 한 줄

## 라이선스

학습용. 자유롭게 수정/공유 OK.
