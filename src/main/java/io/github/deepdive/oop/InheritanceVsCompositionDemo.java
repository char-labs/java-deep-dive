package io.github.deepdive.oop;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Effective Java Item 18: "상속보다 컴포지션을 사용하라" 의 고전적인 예시.
 *
 * HashSet의 add(e)는 내부적으로 다른 add를 호출하지 않는다.
 * addAll(c)는 내부적으로 add를 호출한다 (구현 세부사항).
 *
 * 상속(BadCountingSet)은 상위 클래스 구현에 강결합되어 있어
 *   addAll([a,b,c])  →  add 3번 + addAll 1번 → count = 6  (이중 카운트 버그)
 *
 * 조합(GoodCountingSet)은 캡슐화된 위임이라 영향 없음.
 */
@Component
public class InheritanceVsCompositionDemo implements Demo {

    @Override
    public String name() { return "oop.inheritance"; }

    @Override
    public void run() {
        BadCountingSet<String> bad = new BadCountingSet<>();
        bad.addAll(new ArrayList<>(java.util.List.of("a", "b", "c")));
        System.out.println("[Bad - 상속] expected=3, actual=" + bad.getAddCount() + " (이중 카운트 버그)");

        GoodCountingSet<String> good = new GoodCountingSet<>();
        good.addAll(new ArrayList<>(java.util.List.of("a", "b", "c")));
        System.out.println("[Good - 조합] expected=3, actual=" + good.getAddCount());
    }

    /** 상위 클래스의 내부 호출 흐름까지 알아야 올바르게 동작. 캡슐화 위반. */
    static class BadCountingSet<E> extends HashSet<E> {
        private int addCount = 0;
        @Override public boolean add(E e) { addCount++; return super.add(e); }
        @Override public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();          // ← addAll 자체에서도 카운트
            return super.addAll(c);        //    super.addAll → 내부적으로 add 호출 → 또 카운트
        }
        public int getAddCount() { return addCount; }
    }

    /** 위임만 한다. 상위 클래스의 구현이 바뀌어도 카운트 정확. */
    static class GoodCountingSet<E> {
        private final Set<E> delegate = new HashSet<>();
        private int addCount = 0;
        public boolean add(E e) { addCount++; return delegate.add(e); }
        public boolean addAll(Collection<? extends E> c) {
            boolean changed = false;
            for (E e : c) changed |= add(e);   // 우리 add를 호출 → 한 번만 카운트
            return changed;
        }
        public int getAddCount() { return addCount; }
    }
}
