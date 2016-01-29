package com.github.spriet2000.handlers.tests;

import com.github.spriet2000.handlers.Handlers;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.github.spriet2000.handlers.Handlers.compose;
import static org.junit.Assert.assertEquals;

public class HandlersTest {

    @Test
    public void testExample() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<StringBuilder> handlers = compose(
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

        StringBuilder builder1 = new StringBuilder();
        handler.accept(builder1);

        StringBuilder builder2 = new StringBuilder();
        handler.accept(builder2);

        assertEquals("123", builder1.toString());
        assertEquals("123", builder2.toString());

        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }


    @Test
    public void testException() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<Void> handlers = compose(
                (f, n) -> n::accept,
                (f, n) -> a -> f.accept(new RuntimeException()),
                (f, n) -> n::accept);

        Consumer<Void> handler = handlers.apply(
                a -> hitException.set(true),
                a -> hitComplete.set(true));

        handler.accept(null);

        assertEquals(true, hitException.get());
        assertEquals(false, hitComplete.get());
    }


    public class ExampleHandler implements BiFunction<BiConsumer<StringBuilder, Throwable>,
            Consumer<StringBuilder>, Consumer<StringBuilder>> {

        @Override
        public Consumer<StringBuilder> apply(BiConsumer<StringBuilder, Throwable> fail,
                                                     Consumer<StringBuilder> next) {
            return next::accept;
        }
    }
}
