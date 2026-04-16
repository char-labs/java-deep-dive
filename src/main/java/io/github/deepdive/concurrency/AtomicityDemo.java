package io.github.deepdive.concurrency;

import io.github.deepdive.common.Demo;
import io.github.deepdive.common.Stopwatch;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * count++ 의 원자성 문제와 세 가지 해결책의 성능을 한 번에 비교한다.
 *   1. 그냥 long           → 결과 손실 (값이 작아짐)
 *   2. synchronized        → 정확하지만 락 경쟁으로 느림
 *   3. AtomicLong (CAS)    → 정확하고 보통 더 빠름
 */
@Component
public class AtomicityDemo implements Demo {

    private static final int THREADS = 8;
    private static final int INC_PER_THREAD = 1_000_000;
    private static final long EXPECTED = (long) THREADS * INC_PER_THREAD;

    private long plain;
    private long synced;
    private final Object lock = new Object();
    private final AtomicLong atomic = new AtomicLong();

    @Override
    public String name() { return "concurrency.atomicity"; }

    @Override
    public void run() throws Exception {
        System.out.printf("스레드=%d, 스레드당 증가=%,d, 기대값=%,d%n%n", THREADS, INC_PER_THREAD, EXPECTED);

        runCase("plain long (원자성 위반)", () -> plain++, () -> plain);
        plain = 0;

        runCase("synchronized", () -> {
            synchronized (lock) { synced++; }
        }, () -> synced);
        synced = 0;

        runCase("AtomicLong (CAS)", atomic::incrementAndGet, atomic::get);
        atomic.set(0);
    }

    private void runCase(String label, Runnable inc, java.util.function.LongSupplier read) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        Stopwatch sw = new Stopwatch();
        for (int t = 0; t < THREADS; t++) {
            pool.submit(() -> {
                for (int i = 0; i < INC_PER_THREAD; i++) inc.run();
            });
        }
        pool.shutdown();
        pool.awaitTermination(60, TimeUnit.SECONDS);
        long actual = read.getAsLong();
        System.out.printf("  %-26s 결과=%,d (손실=%,d)  소요=%.1fms%n",
            label, actual, EXPECTED - actual, sw.elapsedMillis());
    }
}
