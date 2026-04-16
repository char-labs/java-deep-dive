package io.github.deepdive.gc;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * "컬렉션에 무한정 데이터가 쌓이는" 메모리 누수 전형을 재현하여 OOM을 발생시킨다.
 *
 * 권장 실행: -Xmx128m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=build/heapdump.hprof
 *
 * 결과로 나오는 hprof를 MAT(Eclipse Memory Analyzer)로 열면
 * Leak Suspects Report에서 ArrayList의 거대한 retained heap이 잡힌다.
 *
 * 주의: OOM이 의도적으로 발생하므로 프로세스가 종료된다.
 */
@Component
public class OomDemo implements Demo {

    @Override
    public String name() { return "gc.oom"; }

    @Override
    public void run() {
        System.out.println("OOM 시뮬레이션 시작. -Xmx128m 권장.");
        List<byte[]> leak = new ArrayList<>();
        try {
            int i = 0;
            while (true) {
                leak.add(new byte[1024 * 1024]); // 1MB
                i++;
                if (i % 16 == 0) {
                    System.out.println("allocated " + i + " MB, list size=" + leak.size());
                }
            }
        } catch (OutOfMemoryError e) {
            System.err.println("OOM 발생: " + e.getMessage());
            System.err.println("힙 덤프가 build/heapdump.hprof 에 생성되었는지 확인하세요.");
            // leak 참조를 살려둬야 reachable로 잡혀 분석 가능. 의도적으로 size만 출력.
            System.err.println("최종 leak.size = " + leak.size());
        }
    }
}
