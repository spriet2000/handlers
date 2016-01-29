package com.github.spriet2000.handlers.tests;

import com.github.spriet2000.handlers.BiHandlers;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.github.spriet2000.handlers.BiHandlers.compose;
import static org.junit.Assert.assertEquals;

public class BiHandlersTest {

    @Test
    public void testExample() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        BiHandlers<StringBuilder, Void> handlers = compose(
                (f, n) -> (e, a) -> {
                    e.append("1");
                    n.accept(e, a);
                }, (f, n) -> (e, a) -> {
                    e.append("2");
                    n.accept(e, a);
                }, (f, n) -> (e, a) -> {
                    e.append("3");
                    n.accept(e, a);
                });

        BiConsumer<StringBuilder, Void> handler = handlers.apply(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true));

        StringBuilder builder1 = new StringBuilder();

        handler.accept(builder1, null);

        assertEquals("123", builder1.toString());

        StringBuilder builder2 = new StringBuilder();

        handler.accept(builder2, null);

        assertEquals("123", builder2.toString());

        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testException() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        BiHandlers<StringBuilder, Void> handlers = compose(
                (f, n) -> n::accept,
                (f, n) -> (e, a) -> f.accept(e, new RuntimeException()),
                (f, n) -> n::accept);

        BiConsumer<StringBuilder, Void> handler = handlers.apply(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true));

        handler.accept(null, null);

        assertEquals(true, hitException.get());
        assertEquals(false, hitComplete.get());
    }

    public class ExampleHandler<StringBuilder> implements  BiFunction<BiConsumer<StringBuilder, Throwable>,
            BiConsumer<StringBuilder, Void>, BiConsumer<StringBuilder, Void>> {

        @Override
        public BiConsumer<StringBuilder, Void> apply(BiConsumer<StringBuilder, Throwable> fail,
                                                     BiConsumer<StringBuilder, Void> next) {
            return next::accept;
        }
    }

}
