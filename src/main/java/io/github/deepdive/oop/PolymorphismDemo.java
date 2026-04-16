package io.github.deepdive.oop;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 다형성 vs instanceof 분기 비교.
 *
 * Bad: instanceof로 타입을 직접 분기 → 타입 추가 시 if-else 수정 필요 (OCP 위반).
 * Good: 다형성으로 각 타입이 자기 fee 계산 책임을 가짐 → 타입 추가가 곧 새 클래스 추가.
 *
 * Java 17+의 sealed + pattern matching switch 도 함께 보여준다 (preview features).
 */
@Component
public class PolymorphismDemo implements Demo {

    @Override
    public String name() { return "oop.polymorphism"; }

    @Override
    public void run() {
        List<Payment> payments = List.of(
            new CardPayment(new BigDecimal("10000")),
            new CashPayment(new BigDecimal("10000")),
            new TransferPayment(new BigDecimal("10000"))
        );

        System.out.println("[Bad] instanceof 분기:");
        for (Payment p : payments) {
            System.out.println("  " + p.getClass().getSimpleName() + " 수수료=" + badFee(p));
        }

        System.out.println("\n[Good] 다형성:");
        for (Payment p : payments) {
            System.out.println("  " + p.getClass().getSimpleName() + " 수수료=" + p.fee());
        }
    }

    /** 새 타입 추가 시 여기 분기를 매번 손봐야 함. OCP 위반. */
    private BigDecimal badFee(Payment p) {
        if (p instanceof CardPayment c) return c.amount().multiply(new BigDecimal("0.025"));
        if (p instanceof CashPayment) return BigDecimal.ZERO;
        if (p instanceof TransferPayment t) return t.amount().multiply(new BigDecimal("0.005"));
        throw new IllegalStateException("unknown payment");
    }

    sealed interface Payment permits CardPayment, CashPayment, TransferPayment {
        BigDecimal amount();
        BigDecimal fee();
    }

    record CardPayment(BigDecimal amount) implements Payment {
        @Override public BigDecimal fee() { return amount.multiply(new BigDecimal("0.025")); }
    }
    record CashPayment(BigDecimal amount) implements Payment {
        @Override public BigDecimal fee() { return BigDecimal.ZERO; }
    }
    record TransferPayment(BigDecimal amount) implements Payment {
        @Override public BigDecimal fee() { return amount.multiply(new BigDecimal("0.005")); }
    }
}
