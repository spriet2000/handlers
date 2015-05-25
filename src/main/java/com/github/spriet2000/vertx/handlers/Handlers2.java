package com.github.spriet2000.vertx.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class Handlers2<E1, E2,
        CustomHandler extends Handler2<E1, E2>,
        CustomController extends Controller2<CustomHandler>> {

    private List<CustomController> controllers;
    private Handler2 exceptionHandler;
    private Handler2 completeHandler;
    private Handler2 handler;
    private Supplier<E2> argsConstructor;

    public Handlers2(Handlers2 controllers) {
        then(controllers);
    }

    public Handlers2(CustomController... controllers) {
        then(controllers);
    }

    public Handlers2 then(CustomController... handlers) {
        Collections.addAll(list(), handlers);
        return this;
    }

    public Handlers2 then(Handlers2 handlers) {
        with(handlers.args());
        exceptionHandler((CustomHandler)handlers.exceptionHandler());
        completeHandler((CustomHandler)handlers.completeHandler());
        list().addAll(handlers.list());
        return this;
    }

    protected Handler2 exceptionHandler() {
        return exceptionHandler;
    }

    public Handlers2 exceptionHandler(CustomHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public Handlers2 exceptionHandler(Handlers2 exceptionHandler) {
        this.exceptionHandler = exceptionHandler.handler();
        return this;
    }

    protected Handler2 completeHandler() {
        return completeHandler;
    }

    public Handlers2 completeHandler(CustomHandler completeHandler) {
        this.completeHandler = completeHandler;
        return this;
    }

    public Handlers2 completeHandler(Handlers2 completeHandler) {
        this.completeHandler = completeHandler.handler();
        return this;
    }

    public List<CustomController> list() {
        if (controllers == null) {
            controllers = new ArrayList<>();
        }
        return controllers;
    }

    public Handlers2 with(Supplier<E2> supplier) {
        this.argsConstructor = supplier;
        return this;
    }

    public Supplier<E2> args() {
        return argsConstructor;
    }

    public void handle(E1 event1, E2 arguments) {
        if (handler == null) {
            handler = handler();
        }
        handler.handle(event1, arguments);
    }

    public Handler2 handler() {
        return (event1, event2) -> {
            Handler2 complete;
            if (completeHandler() == null) {
                complete = (e1, e2) -> {
                };
            } else {
                complete = completeHandler::handle;
            }
            Handler2 fail;
            if (exceptionHandler() == null) {
                fail = (e1, e2) -> {
                };
            } else {
                fail = exceptionHandler::handle;
            }
            Handler2 last = (e1, e2) -> {
            };
            final AtomicBoolean stop = new AtomicBoolean(false);
            for (int i = list().size() - 1; i >= 0; i--) {
                final Handler2 previous = last;
                last = list().get(i).handle(e2 -> {
                    stop.set(true);
                    fail.handle(event1, e2);
                }, e2 -> {
                    if (!stop.get()) previous.handle(event1, e2);
                });
            }
            last.handle(event1, event2);
            complete.handle(event1, event2);
        };
    }

    public static Handlers2 merge(Handlers2 handler1, Handlers2 handler2) {
        handler2.with(handler1.args());
        handler2.exceptionHandler(handler1.exceptionHandler());
        handler2.completeHandler(handler1.completeHandler());
        int index = 0;
        for (Object o : handler1.list()) {
            handler2.list().add(index++, o);
        }
        return handler2;
    }

    public static Handlers2 merge(Handlers2 handler1, Handlers2 handler2, Handlers2 merged) {
        merged.with(handler1.args());
        if (handler2.args() != null){
            merged.with(handler2.args());
        }
        if (handler2.exceptionHandler() != null){
            merged.exceptionHandler(handler2.exceptionHandler());
        }
        merged.exceptionHandler(handler1.exceptionHandler());
        if (handler2.exceptionHandler() != null){
            merged.exceptionHandler(handler2.exceptionHandler());
        }
        merged.completeHandler(handler1.completeHandler());
        if (handler2.completeHandler() != null) {
            merged.completeHandler(handler2.completeHandler());
        }
        merged.list().addAll(handler1.list());
        merged.list().addAll(handler2.list());
        return merged;
    }
}
