package com.github.spriet2000.handlers.tests;

import com.github.spriet2000.handlers.Handlers;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;

public class HandlersTest {

    @Test
    public void testExample() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        StringBuilder builder = new StringBuilder();

        Handlers<StringBuilder> handlers = new Handlers<>(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true),
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

        handlers.accept(builder, null);

        assertEquals("123", builder.toString());
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testCompleteSuccess() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        StringBuilder builder = new StringBuilder();

        Handlers<StringBuilder> handlers = new Handlers<>(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true),
                (f, n) -> (e, a) -> n.accept(null),
                (f, n) -> (e, a) -> n.accept(null),
                (f, n) -> (e, a) -> n.accept(null));

        handlers.accept(builder, null);

        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testCompleteException() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        StringBuilder builder = new StringBuilder();

        Handlers<StringBuilder> handlers = new Handlers<>(
                (e, a) -> hitException.set(true),
                (e, a) -> hitComplete.set(true),
                (f, n) -> (e, a) -> n.accept(null),
                (f, n) -> (e, a) -> f.accept(new RuntimeException()),
                (f, n) -> (e, a) -> {});

        handlers.accept(builder, null);

        assertEquals(true, hitException.get());
        assertEquals(false, hitComplete.get());
    }
}
