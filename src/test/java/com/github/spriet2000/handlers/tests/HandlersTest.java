package com.github.spriet2000.handlers.tests;

import com.github.spriet2000.handlers.Handlers;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
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
                    n.accept(null);
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
    public void testCompleteSuccess() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<Void> handlers = compose(
                (f, n) -> a -> n.accept(null),
                (f, n) -> a -> n.accept(null),
                (f, n) -> a -> n.accept(null));

        Consumer<Void> handler = handlers.apply(
                a -> hitException.set(true),
                a -> hitComplete.set(true));

        handler.accept(null);

        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testCompleteException() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<Void> handlers = compose(
                (f, n) -> a -> n.accept(null),
                (f, n) -> a -> f.accept(new RuntimeException()),
                (f, n) -> a -> {
                });

        Consumer<Void> handler = handlers.apply(
                a -> hitException.set(true),
                a -> hitComplete.set(true));

        handler.accept(null);

        assertEquals(true, hitException.get());
        assertEquals(false, hitComplete.get());
    }

}
