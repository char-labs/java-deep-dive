package io.github.deepdive.common;

/** System.nanoTime 기반 간단 타이머. JMH 까지 갈 필요 없는 데모용. */
public final class Stopwatch {
    private final long start = System.nanoTime();

    public long elapsedNanos() { return System.nanoTime() - start; }
    public double elapsedMillis() { return elapsedNanos() / 1_000_000.0; }

    public static double measureMillis(Runnable r) {
        Stopwatch sw = new Stopwatch();
        r.run();
        return sw.elapsedMillis();
    }
}
