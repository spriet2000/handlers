package com.github.spriet2000.vertx.handlers.tests;

import com.github.spriet2000.vertx.handlers.Handlers;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;

public class BiHandlerTest {

    @Test
    public void testExample() {

        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        StringBuilder builder = new StringBuilder();

        Handlers<StringBuilder> handlers = new Handlers<>(
                f -> hitException.set(true),
                n -> hitComplete.set(true),
                (f, n) -> e -> {
                    e.append("1");
                    n.handle(e);
                }, (f, n) -> e -> {
                    e.append("2");
                    n.handle(e);
                }, (f, n) -> e -> {
                    e.append("3");
                    n.handle(e);
                });
        handlers.handle(builder);

        assertEquals("123", builder.toString());
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOrder() {
        StringBuilder builder = new StringBuilder();
        Handlers<StringBuilder> handlers = new Handlers<>(
                e -> {},
                e -> {},
                (f, n) -> e -> {
                    e.append("1");
                    n.handle(e);
                }, (f, n) -> e -> {
                    e.append("2");
                    n.handle(e);
                }, (f, n) -> e -> {
                    e.append("3");
                    n.handle(e);
                });
        handlers.handle(builder);
        assertEquals("123", builder.toString());
    }

    @Test
    public void testCompleteSuccess() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        StringBuilder builder = new StringBuilder();

        Handlers<StringBuilder> handlers = new Handlers<>(
                f -> hitException.set(true),
                n -> hitComplete.set(true),
                (f, n) -> n::handle,
                (f, n) -> n::handle,
                (f, n) -> n::handle);

        handlers.handle(builder);

        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testCompleteException() {
        AtomicBoolean hitException = new AtomicBoolean(true);
        AtomicBoolean hitComplete = new AtomicBoolean(false);

        StringBuilder builder = new StringBuilder();

        Handlers<StringBuilder> handlers = new Handlers<>(
                f -> hitException.set(true),
                n -> hitComplete.set(true),
                (f, n) -> n::handle,
                (f, n) -> e -> f.handle(new RuntimeException()),
                (f, n) -> n::handle);

        handlers.handle(builder);

        assertEquals(true, hitException.get());
        assertEquals(false, hitComplete.get());
    }
}
