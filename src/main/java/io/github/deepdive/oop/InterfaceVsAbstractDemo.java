package io.github.deepdive.oop;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

/**
 * 인터페이스 vs 추상 클래스.
 *
 * - 인터페이스: 상태 X, 다중 구현 가능, default 메서드로 일부 기본 동작 제공
 * - 추상 클래스: 상태 O, 단일 상속, "공통 상태가 의미 있을 때" 사용
 *
 * 여기서는 같은 요구사항(공통 텍스트 헤더 + 본문 출력)을 두 방식으로 풀어본다.
 */
@Component
public class InterfaceVsAbstractDemo implements Demo {

    @Override
    public String name() { return "oop.interface-abstract"; }

    @Override
    public void run() {
        Reportable a = new InterfaceReport("daily-2026-04-15");
        a.print();

        System.out.println();

        AbstractReport b = new AbstractReport("daily-2026-04-15") {
            @Override protected String body() { return "주문 1,234건 / 매출 56,789,000원"; }
        };
        b.print();
    }

    /** 인터페이스 + default 메서드: 상태가 없어 결합도가 낮고 다중 구현이 가능. */
    interface Reportable {
        String title();
        String body();
        default void print() {
            System.out.println("=== " + title() + " ===");
            System.out.println(body());
        }
    }

    static class InterfaceReport implements Reportable {
        private final String title;
        InterfaceReport(String title) { this.title = title; }
        @Override public String title() { return title; }
        @Override public String body() { return "인터페이스 기반 - 상태는 자기 클래스가 가짐"; }
    }

    /** 추상 클래스: 공통 상태(title)을 상위에서 보유. 템플릿 메서드 패턴에 적합. */
    static abstract class AbstractReport {
        protected final String title;
        protected AbstractReport(String title) { this.title = title; }
        protected abstract String body();
        public final void print() {                 // 템플릿 메서드 (변경 금지)
            System.out.println("=== " + title + " ===");
            System.out.println(body());
        }
    }
}
