package com.github.spriet2000.handlers.tests;

import com.github.spriet2000.handlers.BiHandlers;
import com.github.spriet2000.handlers.Handlers;
import org.openjdk.jmh.annotations.Benchmark;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class BenchMark1 {

    @Benchmark
    public void reportHandlers() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<StringBuilder> handlers = Handlers.compose(
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

    @Benchmark
    public void reportBiHandlers() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        BiHandlers<StringBuilder, Void> handlers = BiHandlers.compose(
                (f, n) -> (e, a) -> n.accept(a),
                (f, n) -> (e, a) -> n.accept(a),
                (f, n) -> (e, a) -> n.accept(a));

        BiConsumer<StringBuilder, Void> handler = handlers.apply(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true));

        for (int i = 0; i < 10000; i++) {
            handler.accept(new StringBuilder(), null);
        }
    }
}