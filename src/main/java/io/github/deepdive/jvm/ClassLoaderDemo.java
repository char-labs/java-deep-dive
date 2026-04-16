package io.github.deepdive.jvm;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

/**
 * 클래스 로더 위계와 "필요한 시점에 로딩"을 보여주는 데모.
 * String, ArrayList, 우리 코드의 ClassLoader 를 출력해보면
 *   - 부트스트랩(자바 표준 라이브러리) → null
 *   - 플랫폼/시스템(애플리케이션 코드) → 인스턴스
 * 의 차이를 확인할 수 있다.
 */
@Component
public class ClassLoaderDemo implements Demo {

    @Override
    public String name() { return "jvm.classloader"; }

    @Override
    public void run() {
        System.out.println("String           => " + String.class.getClassLoader());
        System.out.println("java.util.List   => " + java.util.List.class.getClassLoader());
        System.out.println("javax.sql.DataSource => " + javax.sql.DataSource.class.getClassLoader());
        System.out.println("ClassLoaderDemo  => " + ClassLoaderDemo.class.getClassLoader());

        // 클래스 로딩이 "필요한 시점"에 일어남을 확인
        System.out.println("\nLazy 클래스 로딩 데모: 아래 한 줄 직전까지 LazyLoaded는 로딩되지 않음.");
        long before = ManagementUtil.loadedClassCount();
        LazyLoaded.greet();
        long after = ManagementUtil.loadedClassCount();
        System.out.println("loaded class count: " + before + " → " + after);
    }

    static class LazyLoaded {
        static { System.out.println("[LazyLoaded] static initializer 실행 (이때 클래스가 초기화됨)"); }
        static void greet() { System.out.println("[LazyLoaded] greet() 호출"); }
    }
}
