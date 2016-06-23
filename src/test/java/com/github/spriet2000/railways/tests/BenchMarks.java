package com.github.spriet2000.railways.tests;

import com.github.spriet2000.railways.Railway;
import com.github.spriet2000.railways.Railways;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


public class BenchMarks {

    @Benchmark
    public void report() {
        AtomicBoolean hitStop = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Railway<StringBuilder> railway = Railways.build(
                (f, n) -> n,
                (f, n) -> n,
                (f, n) -> n);

        Consumer<StringBuilder> consumer = railway.apply(
                a -> hitStop.set(true),
                a -> hitComplete.set(true));

        for (int i = 0; i < 10000; i++) {
            consumer.accept(new StringBuilder());
        }
    }
}