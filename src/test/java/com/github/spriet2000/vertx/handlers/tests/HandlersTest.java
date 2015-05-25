package com.github.spriet2000.vertx.handlers.tests;

import com.github.spriet2000.vertx.handlers.Controller;
import com.github.spriet2000.vertx.handlers.Handlers;
import io.vertx.core.Handler;
import io.vertx.test.core.HttpTestBase;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unchecked")
public class HandlersTest extends HttpTestBase {

    @Test
    public void testCompleteSuccess() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        Handlers handlers1 = new Handlers(
                (fail, next) -> (e1 -> next.handle(null)),
                (fail, next) -> (e1 -> next.handle(null)))
                .exceptionHandler((Handler) e1 -> hitException.set(true))
                .completeHandler((Handler) e1 -> hitComplete.set(true));
        handlers1.handle(null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testCompleteFail() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        Handlers handlers1 = new Handlers(
                (fail, next) -> (e1 -> next.handle(null)),
                (fail, next) -> (e1 -> fail.handle(true)))
                .exceptionHandler((Handler) e1 -> hitException.set(true))
                .completeHandler((Handler) e1 -> hitComplete.set(true));
        handlers1.handle(null);
        assertEquals(true, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOrderSuccess() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers handlers1 = new Handlers(
                (fail, next) -> (e1 -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("2");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("3");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("4");
                    next.handle(null);
                }))
                .exceptionHandler((Handler) e1 -> hitException.set(true))
                .completeHandler((Handler) e1 -> {
                    hitComplete.set(true);
                    assertEquals("1234", builder.toString());
                });
        handlers1.handle(null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOrderHandlerAfterThenSuccess() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers handlers1 = new Handlers(
                (fail, next) -> (e1 -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("2");
                    next.handle(null);
                }));
        handlers1.then(
                (fail, next) -> (e1 -> {
                    builder.append("3");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("4");
                    next.handle(null);
                }))
                .exceptionHandler((Handler) e1 -> hitException.set(true))
                .completeHandler((Handler) e1 -> {
                    hitComplete.set(true);
                    assertEquals("1234", builder.toString());
                });
        handlers1.handle(null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOrderFail() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers handlers1 = new Handlers(
                (fail, next) -> (e1 -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("2");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("3");
                    fail.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("4");
                    next.handle(null);
                }))
                .exceptionHandler((Handler) e1 -> hitException.set(true))
                .completeHandler((Handler) e1 -> {
                    hitComplete.set(true);
                    assertEquals("123", builder.toString());
                });
        handlers1.handle(null);
        assertEquals(true, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOrderAfterThenFail() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers handlers1 = new Handlers(
                (fail, next) -> (e1 -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("2");
                    next.handle(null);
                }));
        handlers1.then(
                (fail, next) -> (e1 -> {
                    builder.append("3");
                    fail.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("4");
                    next.handle(null);
                }))
                .exceptionHandler((Handler) e1 -> hitException.set(true))
                .completeHandler((Handler) e1 -> {
                    hitComplete.set(true);
                    assertEquals("123", builder.toString());
                });
        handlers1.handle(null);
        assertEquals(true, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOneSuccess() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        Handlers handlers1 = new Handlers(
                (Controller) (fail, next) -> e1 -> next.handle(null))
            .exceptionHandler((Handler) e1 -> hitException.set(true))
            .completeHandler((Handler) e1 -> hitComplete.set(true));
        handlers1.handle(null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testMerge() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers handlers1 = new Handlers(
                (fail, next) -> (e1 -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("2");
                    next.handle(null);
                }))
                .exceptionHandler((Handler) e1 -> hitException.set(true))
                .completeHandler((Handler) e1 -> {
                    hitComplete.set(true);
                    assertEquals("1234", builder.toString());
                });
        Handlers Handlers = new Handlers(
                (fail, next) -> (e1 -> {
                    builder.append("3");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("4");
                    next.handle(null);
                }));
        Handlers handlers3 = Handlers.merge(handlers1, Handlers);
        handlers3.handle(null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testList() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers handlers1 = new Handlers(
                (fail, next) -> (e1 -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("2");
                    next.handle(null);
                }))
                .exceptionHandler((Handler) e1 -> hitException.set(true))
                .completeHandler((Handler) e1 -> {
                    hitComplete.set(true);
                    assertEquals("1234", builder.toString());
                });
        Handlers handlers2 = new Handlers(
                (fail, next) -> (e1 -> {
                    builder.append("3");
                    next.handle(null);
                }),
                (fail, next) -> (e1 -> {
                    builder.append("4");
                    next.handle(null);
                }));
        Handlers handlers3 = Handlers.merge(handlers1, handlers2);
        handlers1.list().clear();
        assertEquals(4, handlers3.list().size());
        handlers3.handle(null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
        assertEquals(4, handlers3.list().size());
    }
}
