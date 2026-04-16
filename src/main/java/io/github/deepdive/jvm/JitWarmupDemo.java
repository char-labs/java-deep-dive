package io.github.deepdive.jvm;

import io.github.deepdive.common.Demo;
import io.github.deepdive.common.Stopwatch;
import org.springframework.stereotype.Component;

/**
 * "서버 부팅 직후 응답이 느린 이유"와 "JVM Warming up"을 눈으로 보여주는 데모.
 *
 * 같은 메서드를 반복 호출하면서 batch 단위 실행 시간을 찍어보면,
 *   - 초반: 인터프리터로 실행 → 느림
 *   - C1 컴파일 임계치(약 2,000회) 통과 → 빨라짐
 *   - C2 컴파일 임계치(약 15,000회) 통과 → 더 빨라짐
 * 형태로 단계적인 속도 향상을 관찰할 수 있다.
 *
 * 컴파일 이벤트를 직접 보고 싶다면 다음 옵션을 추가해서 실행하라.
 *   -XX:+PrintCompilation
 *   -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
 */
@Component
public class JitWarmupDemo implements Demo {

    @Override
    public String name() { return "jvm.jit"; }

    @Override
    public void run() {
        final int batches = 30;
        final int callsPerBatch = 2_000;

        long sink = 0L; // JIT가 결과를 dead code로 제거하지 못하도록 소비
        System.out.printf("%-6s | %-10s | %-12s%n", "batch", "totalCalls", "elapsedMs");
        System.out.println("-".repeat(40));

        for (int b = 1; b <= batches; b++) {
            Stopwatch sw = new Stopwatch();
            for (int i = 0; i < callsPerBatch; i++) {
                sink += hotMethod(i);
            }
            double ms = sw.elapsedMillis();
            System.out.printf("%-6d | %-10d | %-12.3f%n", b, b * callsPerBatch, ms);
        }
        System.out.println("sink=" + sink + " (JIT가 최적화로 코드를 제거하지 않게 하기 위한 소비값)");
        System.out.println("\n해석: 초반 batch 시간이 후반 batch 시간보다 큰 폭으로 길다면 JIT 워밍업이 일어난 것이다.");
    }

    /**
     * 의도적으로 적당한 비용의 산술 연산을 수행한다.
     * inline-able 하면서 JIT 최적화 여지가 있는 패턴.
     */
    private static long hotMethod(int x) {
        long acc = 0;
        for (int i = 0; i < 100; i++) {
            acc += (long) x * i + (i ^ x);
        }
        return acc;
    }
}
