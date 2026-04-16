package io.github.deepdive.jvm;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * "JIT 컴파일러가 인터프리터 대비 얼마나 빠른가"를 측정.
 *
 * 한 벤치마크는 워밍업 없이(-XX:-TieredCompilation 으로 인터프리터 강제), 다른 하나는 기본 JIT.
 * JMH의 fork별 jvmArgs로 분리한다.
 *
 * 실행: ./gradlew jmh -Pjmh.includes=InterpreterVsJitBenchmark
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class InterpreterVsJitBenchmark {

    private int[] data;

    @Setup
    public void setup() {
        data = new int[10_000];
        for (int i = 0; i < data.length; i++) data[i] = i;
    }

    @Benchmark
    @Fork(value = 1, jvmArgsAppend = {
        // JIT 비활성화 → 사실상 인터프리터로만 실행
        "-Xint"
    })
    public void interpreterOnly(Blackhole bh) {
        bh.consume(sum(data));
    }

    @Benchmark
    @Fork(value = 1)
    public void withJit(Blackhole bh) {
        bh.consume(sum(data));
    }

    private static long sum(int[] arr) {
        long s = 0;
        for (int v : arr) s += v;
        return s;
    }
}
