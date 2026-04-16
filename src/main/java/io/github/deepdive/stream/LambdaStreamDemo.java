package io.github.deepdive.stream;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 람다 + 스트림이 "어떻게(how)" 대신 "무엇을(what)"을 표현하는지 보여주는 비교 데모.
 */
@Component
public class LambdaStreamDemo implements Demo {

    record Order(String product, String category, int amount) {}

    @Override
    public String name() { return "stream.basic"; }

    @Override
    public void run() {
        List<Order> orders = List.of(
            new Order("apple", "FOOD", 1000),
            new Order("book", "BOOK", 12000),
            new Order("rice", "FOOD", 30000),
            new Order("novel", "BOOK", 8000),
            new Order("milk", "FOOD", 4000)
        );

        // [명령형] - 어떻게 할지 단계별로 기술
        Map<String, Integer> imperative = new java.util.HashMap<>();
        for (Order o : orders) {
            if (o.amount() >= 5000) {
                imperative.merge(o.category(), o.amount(), Integer::sum);
            }
        }
        System.out.println("[명령형] " + imperative);

        // [선언형 - 람다/스트림] - 무엇을 할지를 표현
        Map<String, Integer> declarative = orders.stream()
            .filter(o -> o.amount() >= 5000)
            .collect(Collectors.groupingBy(Order::category, Collectors.summingInt(Order::amount)));
        System.out.println("[선언형] " + declarative);
    }
}
