package io.github.deepdive.gc;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Young 영역에 단명(短命) 객체를 폭발적으로 생성하여 Minor GC를 유도하고,
 * GC 카운트와 누적 시간이 늘어나는 것을 측정한다.
 *
 * 권장 실행 옵션:
 *   ./gradlew bootRun --args='gc.minor' \
 *     -Dorg.gradle.jvmargs='-Xms256m -Xmx256m -Xlog:gc*'
 *
 * 또는 build.gradle.kts 의 bootRun jvmArgs 주석 해제.
 */
@Component
public class MinorGcDemo implements Demo {

    @Override
    public String name() { return "gc.minor"; }

    @Override
    public void run() {
        Map<String, long[]> before = snapshot();

        // 단명 객체 폭발: 매 루프마다 새 byte[1KB]를 만들고 즉시 버린다.
        long total = 0;
        for (int i = 0; i < 5_000_000; i++) {
            byte[] b = new byte[1024];
            total += b.length;
        }
        System.out.println("총 할당 바이트: " + total);

        Map<String, long[]> after = snapshot();
        System.out.printf("%n%-30s %10s %10s%n", "GC", "ΔCount", "ΔTimeMs");
        before.forEach((name, b) -> {
            long[] a = after.get(name);
            System.out.printf("%-30s %10d %10d%n", name, a[0] - b[0], a[1] - b[1]);
        });
        System.out.println("\n해석: Young 영역(Eden) 위주 GC 카운트가 크게 증가하면 Minor GC가 활발히 동작한 것이다.");
    }

    private Map<String, long[]> snapshot() {
        Map<String, long[]> m = new HashMap<>();
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            m.put(gc.getName(), new long[]{gc.getCollectionCount(), gc.getCollectionTime()});
        }
        return m;
    }
}
