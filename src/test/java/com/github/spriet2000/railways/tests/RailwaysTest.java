package com.github.spriet2000.railways.tests;

import com.github.spriet2000.railways.Handlers;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.github.spriet2000.railways.Handlers.build;
import static org.junit.Assert.assertEquals;

public class RailwaysTest {

    @Test
    public void testExample() {

        AtomicBoolean hitStop = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<StringBuilder> handlers = build(
                (f, n) -> a -> {
                    a.append("1");
                    n.accept(a);
                }, (f, n) -> a -> {
                    a.append("2");
                    n.accept(a);
                }, (f, n) -> a -> {
                    a.append("3");
                    n.accept(a);
                });

        Consumer<StringBuilder> handler = handlers.apply(
                a -> hitStop.set(true),
                a -> hitComplete.set(true));

        StringBuilder builder = new StringBuilder();
        handler.accept(builder);

        assertEquals("123", builder.toString());

        assertEquals(false, hitStop.get());
        assertEquals(true, hitComplete.get());
    }


    @Test
    public void testException() {

        AtomicBoolean hitStop = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<Void> handlers = build(
                (f, n) -> n,
                (f, n) -> a -> f.accept(new RuntimeException()),
                (f, n) -> n);

        Consumer<Void> handler = handlers.apply(
                a -> hitStop.set(true),
                a -> hitComplete.set(true));

        handler.accept(null);

        assertEquals(true, hitStop.get());
        assertEquals(false, hitComplete.get());
    }


    public class Station implements BiFunction<BiConsumer<StringBuilder, Throwable>,
            Consumer<StringBuilder>, Consumer<StringBuilder>> {

        @Override
        public Consumer<StringBuilder> apply(BiConsumer<StringBuilder, Throwable> stop,
                                                     Consumer<StringBuilder> next) {
            return next;
        }
    }
}
