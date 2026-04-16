package io.github.deepdive.concurrency;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

/**
 * 가시성(Visibility) 문제 데모.
 *
 * 메인 스레드가 stop=true 로 바꿔도, JIT 최적화/캐시 일관성 문제로
 * 워커 스레드가 변경을 보지 못해 무한 루프에 빠질 수 있다.
 * volatile 키워드를 붙이면 정상 종료된다.
 *
 * 주의: 이 현상은 JVM 구현/CPU/JIT 타이밍에 따라 항상 재현되지는 않는다.
 *       다만 JLS 상 volatile 없는 변수에는 happens-before 보장이 없다는 사실이 핵심이다.
 */
@Component
public class VisibilityDemo implements Demo {

    private boolean stop = false;             // 가시성 보장 X
    private volatile boolean stopVolatile = false; // 가시성 보장 O

    @Override
    public String name() { return "concurrency.visibility"; }

    @Override
    public void run() throws Exception {
        System.out.println("[case 1] volatile 없음 - 워커가 stop=true를 못 볼 수 있음 (최대 3초 대기)");
        runCase(false);

        System.out.println("\n[case 2] volatile 적용 - 즉시 종료");
        runCase(true);
    }

    private void runCase(boolean useVolatile) throws InterruptedException {
        if (useVolatile) stopVolatile = false; else stop = false;

        Thread worker = new Thread(() -> {
            long loops = 0;
            while (!(useVolatile ? stopVolatile : stop)) {
                loops++;
            }
            System.out.println("  워커 종료. loops=" + loops);
        });
        worker.start();

        Thread.sleep(500);
        if (useVolatile) stopVolatile = true; else stop = true;
        System.out.println("  메인: stop=true 설정 완료");

        worker.join(3000);
        if (worker.isAlive()) {
            System.out.println("  ⚠ 워커가 stop을 못 봤음. 강제 인터럽트.");
            worker.interrupt();
            worker.join();
        }
    }
}
