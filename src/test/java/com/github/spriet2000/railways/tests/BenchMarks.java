package com.github.spriet2000.railways.tests;

import com.github.spriet2000.railways.Handlers;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.github.spriet2000.railways.Handlers.build;


public class BenchMarks {

    @Benchmark
    public void report() {
        AtomicBoolean hitStop = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<StringBuilder> handlers = build(
                (f, n) -> n,
                (f, n) -> n,
                (f, n) -> n);

        Consumer<StringBuilder> consumer = handlers.apply(
                a -> hitStop.set(true),
                a -> hitComplete.set(true));

        for (int i = 0; i < 10000; i++) {
            consumer.accept(new StringBuilder());
        }
    }
}