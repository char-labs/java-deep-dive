package io.github.deepdive.gc;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * "객체 재사용 vs 매번 새 객체 생성" 의 처리량 차이를 측정한다.
 * GC 압력 줄이기, 객체 풀, escape analysis 등을 설명할 때 근거로 활용 가능.
 *
 * 실행: ./gradlew jmh -Pjmh.includes=AllocationBenchmark
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class AllocationBenchmark {

    private final byte[] reused = new byte[1024];

    @Benchmark
    public void allocateNew(Blackhole bh) {
        bh.consume(new byte[1024]);
    }

    @Benchmark
    public void reuse(Blackhole bh) {
        bh.consume(reused);
    }
}
