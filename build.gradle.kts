plugins {
    java
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("me.champeau.jmh") version "0.7.2"
}

group = "io.github"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot 4 - 데모 실행을 위한 CommandLineRunner 진입점만 필요해서 web은 미포함
    implementation("org.springframework.boot:spring-boot-starter")

    // JMH (정식 마이크로벤치마크 - JVM/스트림 측정용)
    jmh("org.openjdk.jmh:jmh-core:1.37")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    jmhAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    // GC 로그 옵션은 여기(bootRun.jvmArgs)에 지정해야 합니다.
    // org.gradle.jvmargs (gradle.properties) 는 Gradle 데몬 자체용이라 절대 넣으면 안 됩니다.
    //
    // GC 로그를 보고 싶을 때 아래 주석을 풀고 실행:
    //   ./gradlew bootRun --args='gc.minor'  (별도 -D 옵션 불필요)
    //
    // jvmArgs = listOf(
    //     "--enable-preview",
    //     "-Xms256m", "-Xmx256m",
    //     "-Xlog:gc*:file=${layout.buildDirectory.get().asFile.absolutePath}/gc.log:time,uptime,level,tags",
    //     "-XX:+HeapDumpOnOutOfMemoryError",
    //     "-XX:HeapDumpPath=${layout.buildDirectory.get().asFile.absolutePath}/heapdump.hprof"
    // )
    jvmArgs = listOf("--enable-preview")
}

jmh {
    warmupIterations = 3
    iterations = 5
    fork = 1
    timeUnit = "ms"
    jvmArgsAppend = listOf("--enable-preview")
}
