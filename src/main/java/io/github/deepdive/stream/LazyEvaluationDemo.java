package io.github.deepdive.stream;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 스트림 지연 연산 데모.
 *
 * 핵심: 중간 연산(filter/map)은 최종 연산이 호출될 때까지 실행되지 않는다.
 * 그리고 limit(N)이 붙으면 N개를 채우는 순간 더 이상 원소를 끌어오지 않는다.
 *
 * filterCount, mapCount 로 실제 호출 횟수를 직접 측정한다.
 */
@Component
public class LazyEvaluationDemo implements Demo {

    @Override
    public String name() { return "stream.lazy"; }

    @Override
    public void run() {
        AtomicInteger filterCount = new AtomicInteger();
        AtomicInteger mapCount = new AtomicInteger();

        var result = IntStream.range(0, 1_000_000)
            .boxed()
            .filter(i -> { filterCount.incrementAndGet(); return i % 2 == 0; })
            .map(i -> { mapCount.incrementAndGet(); return i * 2; })
            .limit(5)
            .toList();

        System.out.println("결과: " + result);
        System.out.println("filter 호출 수: " + filterCount.get() + " (1,000,000 이 아님!)");
        System.out.println("map 호출 수: " + mapCount.get());
        System.out.println("\n해석: limit(5)을 만족하는 순간 파이프라인이 즉시 멈춘다.");
        System.out.println("    또한 원소 1개당 filter→map이 한 번씩 흐르는 단일 패스 처리(loop fusion).");
    }
}
