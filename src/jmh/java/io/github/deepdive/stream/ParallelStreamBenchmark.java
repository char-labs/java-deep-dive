package io.github.deepdive.stream;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

/**
 * sequential vs parallel 스트림 처리량 비교.
 *
 * "parallelStream을 무조건 쓰면 빠르다고 할 수 있나?" 에 대한 데이터.
 * 아래 두 케이스를 비교하면:
 *   - heavyMath: CPU bound 작업이라 parallel이 코어 수만큼 거의 선형 가속
 *   - light:    원소당 작업이 너무 가벼우면 ForkJoin 분할 비용이 커서 오히려 느려질 수 있다
 *
 * 실행: ./gradlew jmh -Pjmh.includes=ParallelStreamBenchmark
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class ParallelStreamBenchmark {

    @Param({"1000000"})
    private int n;

    @Benchmark
    public long heavyMath_sequential() {
        return LongStream.range(0, n).map(ParallelStreamBenchmark::heavy).sum();
    }

    @Benchmark
    public long heavyMath_parallel() {
        return LongStream.range(0, n).parallel().map(ParallelStreamBenchmark::heavy).sum();
    }

    @Benchmark
    public long light_sequential() {
        return LongStream.range(0, n).map(x -> x + 1).sum();
    }

    @Benchmark
    public long light_parallel() {
        return LongStream.range(0, n).parallel().map(x -> x + 1).sum();
    }

    private static long heavy(long x) {
        long s = 0;
        for (int i = 0; i < 50; i++) s += (x * i) ^ (x + i);
        return s;
    }
}
