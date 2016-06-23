package com.github.spriet2000.railways.tests;

import com.github.spriet2000.railways.Railway;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.github.spriet2000.railways.Railways.compose;
import static org.junit.Assert.assertEquals;

public class RailwaysTest {

    @Test
    public void testExample() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Railway<StringBuilder> handlers = compose(
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
                a -> hitException.set(true),
                a -> hitComplete.set(true));

        StringBuilder builder = new StringBuilder();
        handler.accept(builder);

        assertEquals("123", builder.toString());

        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }


    @Test
    public void testException() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Railway<Void> handlers = compose(
                (f, n) -> n,
                (f, n) -> a -> f.accept(new RuntimeException()),
                (f, n) -> n);

        Consumer<Void> handler = handlers.apply(
                a -> hitException.set(true),
                a -> hitComplete.set(true));

        handler.accept(null);

        assertEquals(true, hitException.get());
        assertEquals(false, hitComplete.get());
    }


    public class ExampleRailway implements BiFunction<BiConsumer<StringBuilder, Throwable>,
            Consumer<StringBuilder>, Consumer<StringBuilder>> {

        @Override
        public Consumer<StringBuilder> apply(BiConsumer<StringBuilder, Throwable> fail,
                                                     Consumer<StringBuilder> next) {
            return next;
        }
    }
}
