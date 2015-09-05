package com.github.spriet2000.handlers.tests;

import com.github.spriet2000.handlers.Handlers;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static com.github.spriet2000.handlers.Handlers.compose;
import static org.junit.Assert.assertEquals;

public class HandlersTest {

    @Test
    public void testExample() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        StringBuilder builder = new StringBuilder();

        Handlers<StringBuilder, String> handlers = compose(
                (f, n) -> (e, a) -> {
                    e.append("1");
                    n.accept("A");
                }, (f, n) -> (e, a) -> {
                    assertEquals("A", a);
                    e.append("2");
                    n.accept("B");
                }, (f, n) -> (e, a) -> {
                    assertEquals("B", a);
                    e.append("3");
                    n.accept(null);
                });

        BiConsumer handler = handlers.apply(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true));

        handler.accept(builder, null);

        assertEquals("123", builder.toString());
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }


    @Test
    public void testCompleteSuccess() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        StringBuilder builder = new StringBuilder();

        Handlers<StringBuilder, String> handlers = compose(
                (f, n) -> (e, a) -> n.accept(null),
                (f, n) -> (e, a) -> n.accept(null),
                (f, n) -> (e, a) -> n.accept(null));

        BiConsumer handler = handlers.apply(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true));

        handler.accept(builder, null);

        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testCompleteException() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        StringBuilder builder = new StringBuilder();

        Handlers<StringBuilder, String> handlers = compose(
                (f, n) -> (e, a) -> n.accept(null),
                (f, n) -> (e, a) -> f.accept(new RuntimeException()),
                (f, n) -> (e, a) -> {
                });

        BiConsumer handler = handlers.apply(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true));

        handler.accept(builder, null);

        assertEquals(true, hitException.get());
        assertEquals(false, hitComplete.get());
    }

    @Test
    public void compositionTest() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        Handlers<StringBuilder, String> handlers1 = compose(
                (f, n) -> (e, a) -> {
                    e.append("1");
                    n.accept(a);
                });

        Handlers<StringBuilder, String> handlers2 = compose(
                (f, n) -> (e, a) -> {
                    e.append("2");
                    n.accept(a);
                });

        Handlers<StringBuilder, String> handlers3 = compose(
                (f, n) -> (e, a) -> {
                    e.append("3");
                    n.accept(a);
                });

        StringBuilder builder = new StringBuilder();

        compose(handlers1, handlers2, handlers3)
                .successHandler((e, a) -> hitComplete.set(true))
                .exceptionHandler((e, a) -> hitException.set(true))
                .accept(builder, null);

        assertEquals("123", builder.toString());
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }
}
