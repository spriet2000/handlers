package com.github.spriet2000.handlers.tests;

import com.github.spriet2000.handlers.BiHandlers;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static com.github.spriet2000.handlers.BiHandlers.compose;
import static org.junit.Assert.assertEquals;

public class BiHandlersTest {

    @Test
    public void testExample() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        BiHandlers<StringBuilder, String> handlers = compose(
                (f, n) -> (e, a) -> {
                    e.append("1");
                    n.accept("a");
                }, (f, n) -> (e, a) -> {
                    e.append("2");
                    n.accept("b");
                }, (f, n) -> (e, a) -> {
                    e.append("3");
                    n.accept("c");
                });

        BiConsumer<StringBuilder, String> handler = handlers.apply(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true));

        StringBuilder builder = new StringBuilder();

        handler.accept(builder, null);

        assertEquals("123", builder.toString());

        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }


}
