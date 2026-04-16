package io.github.deepdive.jvm;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;

/** JVM 내부 상태(클래스 로딩 수, 메모리 등) 조회 헬퍼. */
public final class ManagementUtil {
    private ManagementUtil() {}

    public static long loadedClassCount() {
        ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();
        return bean.getLoadedClassCount();
    }
}
