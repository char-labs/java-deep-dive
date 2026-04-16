package io.github.deepdive.concurrency;

import io.github.deepdive.common.Demo;
import io.github.deepdive.common.Stopwatch;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * "왜 Spring MVC가 Context Switching 비용을 감수하면서 수백 스레드를 돌리는가" 데모.
 *
 * 작업 = 50ms blocking I/O 시뮬레이션 (Thread.sleep)
 * 고정 스레드풀 크기를 1, 4, 32, 200 으로 바꿔가며 동일한 200건 작업을 처리한다.
 *
 * 결과: 풀이 작으면 I/O 대기 동안 CPU가 놀면서 총 시간이 길어지고,
 *       풀이 크면 동시에 더 많은 요청이 sleep 상태로 진행되어 총 시간이 짧아진다.
 *       이게 곧 "비싼 CPU를 놀리지 않으려고 스레드를 늘린다"의 실증.
 */
@Component
public class ThreadPoolDemo implements Demo {

    private static final int TASKS = 200;
    private static final long TASK_BLOCK_MS = 50;

    @Override
    public String name() { return "concurrency.threadpool"; }

    @Override
    public void run() throws Exception {
        System.out.printf("작업 수=%d, 작업당 blocking=%dms (이상치 = TASKS × blocking / poolSize)%n%n",
            TASKS, TASK_BLOCK_MS);
        System.out.printf("%-12s | %-12s%n", "poolSize", "elapsedMs");
        System.out.println("-".repeat(30));
        for (int size : new int[]{1, 4, 32, 200}) {
            measure(size);
        }
    }

    private void measure(int poolSize) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);
        CountDownLatch latch = new CountDownLatch(TASKS);
        Stopwatch sw = new Stopwatch();
        IntStream.range(0, TASKS).forEach(i -> pool.submit(() -> {
            try { Thread.sleep(TASK_BLOCK_MS); } catch (InterruptedException ignored) {}
            latch.countDown();
        }));
        latch.await();
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
        System.out.printf("%-12d | %-12.1f%n", poolSize, sw.elapsedMillis());
    }
}
