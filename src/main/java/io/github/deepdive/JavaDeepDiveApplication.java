package io.github.deepdive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import io.github.deepdive.common.Demo;

import java.util.Map;

/**
 * 진입점.
 *
 * 사용법:
 *   ./gradlew bootRun --args='jvm.jit'
 *   ./gradlew bootRun --args='gc.heap'
 *   ./gradlew bootRun --args='concurrency.visibility'
 *
 * 인자가 없으면 사용 가능한 데모 목록을 출력한다.
 *
 * Spring Boot 4 / Java 25 기준. Spring Boot의 의존성/실행 인프라 외에는
 * 데모 코드가 본체이다.
 */
@SpringBootApplication
public class JavaDeepDiveApplication implements CommandLineRunner {

    private final ApplicationContext ctx;

    public JavaDeepDiveApplication(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public static void main(String[] args) {
        SpringApplication.run(JavaDeepDiveApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, Demo> demos = ctx.getBeansOfType(Demo.class);

        if (args.length == 0) {
            System.out.println("사용 가능한 데모:");
            demos.values().stream()
                .map(Demo::name)
                .sorted()
                .forEach(n -> System.out.println("  - " + n));
            System.out.println("\n예) ./gradlew bootRun --args='jvm.jit'");
            return;
        }

        String name = args[0];
        Demo demo = demos.values().stream()
            .filter(d -> d.name().equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown demo: " + name));

        System.out.println("\n=== " + demo.name() + " ===");
        demo.run();
    }
}
