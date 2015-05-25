package com.github.spriet2000.vertx.handlers.tests;

import com.github.spriet2000.vertx.handlers.Controller2;
import com.github.spriet2000.vertx.handlers.Handler2;
import com.github.spriet2000.vertx.handlers.Handlers2;
import io.vertx.test.core.HttpTestBase;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unchecked")
public class Handlers2Test extends HttpTestBase {

    @Test
    public void testCompleteSuccess() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        Handlers2 handlers1 = new Handlers2(
                (fail, next) -> ((e1, e2) -> next.handle(null)),
                (fail, next) -> ((e1, e2) -> next.handle(null)))
                .exceptionHandler((Handler2) (e1, e2) -> hitException.set(true))
                .completeHandler((Handler2) (e1, e2) -> hitComplete.set(true));
        handlers1.handle(null, null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testCompleteFail() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        Handlers2 handlers1 = new Handlers2(
                (fail, next) -> ((e1, e2) -> next.handle(null)),
                (fail, next) -> ((e1, e2) -> fail.handle(true)))
                .exceptionHandler((Handler2) (e1, e2) -> hitException.set(true))
                .completeHandler((Handler2) (e1, e2) -> hitComplete.set(true));
        handlers1.handle(null, null);
        assertEquals(true, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOrderSuccess() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers2 handlers1 = new Handlers2(
                (fail, next) -> ((e1, e2) -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("2");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("3");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("4");
                    next.handle(null);
                }))
                .exceptionHandler((Handler2) (e1, e2) -> hitException.set(true))
                .completeHandler((Handler2) (e1, e2) -> {
                    hitComplete.set(true);
                    assertEquals("1234", builder.toString());
                });
        handlers1.handle(null, null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOrderHandlerAfterThenSuccess() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers2 handlers1 = new Handlers2(
                (fail, next) -> ((e1, e2) -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("2");
                    next.handle(null);
                }));
        handlers1.then(
                (fail, next) -> ((e1, e2) -> {
                    builder.append("3");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("4");
                    next.handle(null);
                }))
                .exceptionHandler((Handler2) (e1, e2) -> hitException.set(true))
                .completeHandler((Handler2) (e1, e2) -> {
                    hitComplete.set(true);
                    assertEquals("1234", builder.toString());
                });
        handlers1.handle(null, null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOrderFail() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers2 handlers1 = new Handlers2(
                (fail, next) -> ((e1, e2) -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("2");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("3");
                    fail.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("4");
                    next.handle(null);
                }))
                .exceptionHandler((Handler2) (e1, e2) -> hitException.set(true))
                .completeHandler((Handler2) (e1, e2) -> {
                    hitComplete.set(true);
                    assertEquals("123", builder.toString());
                });
        handlers1.handle(null, null);
        assertEquals(true, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOrderAfterThenFail() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers2 handlers1 = new Handlers2(
                (fail, next) -> ((e1, e2) -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("2");
                    next.handle(null);
                }));
        handlers1.then(
                (fail, next) -> ((e1, e2) -> {
                    builder.append("3");
                    fail.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("4");
                    next.handle(null);
                }))
                .exceptionHandler((Handler2) (e1, e2) -> hitException.set(true))
                .completeHandler((Handler2) (e1, e2) -> {
                    hitComplete.set(true);
                    assertEquals("123", builder.toString());
                });
        handlers1.handle(null, null);
        assertEquals(true, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testOneSuccess() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        Handlers2 handlers1 = new Handlers2(
                (Controller2) (fail, next) -> (e1, e2) -> next.handle(null))
            .exceptionHandler((Handler2) (e1, e2) -> hitException.set(true))
            .completeHandler((Handler2) (e1, e2) -> hitComplete.set(true));
        handlers1.handle(null, null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testMerge() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers2 handlers1 = new Handlers2(
                (fail, next) -> ((e1, e2) -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("2");
                    next.handle(null);
                }))
                .exceptionHandler((Handler2) (e1, e2) -> hitException.set(true))
                .completeHandler((Handler2) (e1, e2) -> {
                    hitComplete.set(true);
                    assertEquals("1234", builder.toString());
                });
        Handlers2 handlers2 = new Handlers2(
                (fail, next) -> ((e1, e2) -> {
                    builder.append("3");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("4");
                    next.handle(null);
                }));
        Handlers2 handles3 = Handlers2.merge(handlers1, handlers2);
        handles3.handle(null, null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
    }

    @Test
    public void testList() {
        AtomicBoolean hitException = new AtomicBoolean(false);
        AtomicBoolean hitComplete = new AtomicBoolean(false);
        StringBuilder builder = new StringBuilder();
        Handlers2 handlers1 = new Handlers2(
                (fail, next) -> ((e1, e2) -> {
                    builder.append("1");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("2");
                    next.handle(null);
                }))
                .exceptionHandler((Handler2) (e1, e2) -> hitException.set(true))
                .completeHandler((Handler2) (e1, e2) -> {
                    hitComplete.set(true);
                    assertEquals("1234", builder.toString());
                });
        Handlers2 handlers2 = new Handlers2(
                (fail, next) -> ((e1, e2) -> {
                    builder.append("3");
                    next.handle(null);
                }),
                (fail, next) -> ((e1, e2) -> {
                    builder.append("4");
                    next.handle(null);
                }));
        Handlers2 handlers3 = Handlers2.merge(handlers1, handlers2);
        handlers1.list().clear();
        assertEquals(4, handlers3.list().size());
        handlers3.handle(null, null);
        assertEquals(false, hitException.get());
        assertEquals(true, hitComplete.get());
        assertEquals(4, handlers3.list().size());
    }
}
