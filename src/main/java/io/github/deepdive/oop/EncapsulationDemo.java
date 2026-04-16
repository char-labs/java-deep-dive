package io.github.deepdive.oop;

import io.github.deepdive.common.Demo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 캡슐화 위반(Bad) vs 행동 메서드(Good) 비교.
 *
 * Bad: setBalance() 노출 → 외부에서 음수로 만들 수 있음, 불변식 깨짐.
 * Good: deposit/withdraw 행동 메서드 → 객체가 스스로 상태를 관리.
 */
@Component
public class EncapsulationDemo implements Demo {

    @Override
    public String name() { return "oop.encapsulation"; }

    @Override
    public void run() {
        System.out.println("[Bad] setter 무분별 노출:");
        BadAccount bad = new BadAccount();
        bad.setBalance(new BigDecimal("1000"));
        bad.setBalance(new BigDecimal("-9999")); // 외부에서 불변식 위반
        System.out.println("  balance = " + bad.getBalance() + "  → 검증 못 함");

        System.out.println("\n[Good] 행동 메서드(deposit/withdraw):");
        GoodAccount good = new GoodAccount();
        good.deposit(new BigDecimal("1000"));
        try {
            good.withdraw(new BigDecimal("9999")); // 잔액 부족 → 예외
        } catch (IllegalStateException e) {
            System.out.println("  withdraw 거부: " + e.getMessage());
        }
        System.out.println("  balance = " + good.balance() + "  → 항상 ≥ 0 보장");
    }

    static class BadAccount {
        private BigDecimal balance = BigDecimal.ZERO;
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; } // 캡슐화 깨짐
    }

    static class GoodAccount {
        private BigDecimal balance = BigDecimal.ZERO;

        public void deposit(BigDecimal amount) {
            if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");
            balance = balance.add(amount);
        }

        public void withdraw(BigDecimal amount) {
            if (amount.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");
            if (balance.compareTo(amount) < 0) throw new IllegalStateException("insufficient: " + balance);
            balance = balance.subtract(amount);
        }

        public BigDecimal balance() { return balance; } // 읽기 전용 노출
    }
}
