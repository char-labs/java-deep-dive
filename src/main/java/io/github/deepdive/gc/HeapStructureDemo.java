package io.github.deepdive.gc;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

/**
 * 현재 JVM이 사용하는 GC와 Heap의 영역(Young/Old/Metaspace 등)을 출력한다.
 * "Java 11/17/21 디폴트 GC?" 질문이 나올 때 직접 확인 가능.
 */
@Component
public class HeapStructureDemo implements Demo {

    @Override
    public String name() { return "gc.heap"; }

    @Override
    public void run() {
        System.out.println("== Garbage Collectors ==");
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.printf("  %-22s pools=%s collections=%d totalTimeMs=%d%n",
                gc.getName(),
                List.of(gc.getMemoryPoolNames()),
                gc.getCollectionCount(),
                gc.getCollectionTime());
        }

        System.out.println("\n== Memory Pools ==");
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            MemoryUsage u = pool.getUsage();
            System.out.printf("  %-30s type=%-10s used=%-10s max=%-10s%n",
                pool.getName(), pool.getType(), human(u.getUsed()), human(u.getMax()));
        }

        System.out.println("\n해석 가이드:");
        System.out.println("  - G1 Eden/Survivor/Old + Metaspace 가 보이면 G1 GC (Java 11+ 기본)");
        System.out.println("  - PS Eden/Survivor/Old 가 보이면 Parallel GC (Java 8 기본)");
        System.out.println("  - ZGC/Shenandoah Heap 단일 풀이면 region 기반 저지연 콜렉터");
    }

    private static String human(long bytes) {
        if (bytes < 0) return "n/a";
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + "KB";
        return (bytes / (1024 * 1024)) + "MB";
    }
}
