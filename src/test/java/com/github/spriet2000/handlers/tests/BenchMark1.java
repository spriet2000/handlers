package com.github.spriet2000.handlers.tests;

import com.github.spriet2000.handlers.Handlers;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.github.spriet2000.handlers.Handlers.compose;

public class BenchMark1 {

    @Benchmark
    public void report() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<StringBuilder> handlers = compose(
                (f, n) -> n::accept,
                (f, n) -> n::accept,
                (f, n) -> n::accept);

        Consumer<StringBuilder> handler = handlers.apply(
                a -> hitException.set(true),
                a -> hitComplete.set(true));

        for (int i = 0; i < 10000; i++) {
            handler.accept(new StringBuilder());
        }

    }
}