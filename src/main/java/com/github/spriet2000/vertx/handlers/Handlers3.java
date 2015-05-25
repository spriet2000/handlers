package com.github.spriet2000.vertx.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class Handlers3<E1, E2, E3,
        CustomHandler extends Handler3<E1, E2, E3>,
        CustomController extends Controller3<CustomHandler>> {

    private List<CustomController> controllers;
    private Handler3 exceptionHandler;
    private Handler3 completeHandler;
    private Handler3 handler;
    private Supplier<E3> argsConstructor;

    public Handlers3(Handlers3 controllers) {
        then(controllers);
    }

    public Handlers3(CustomController... controllers) {
        then(controllers);
    }

    public Handlers3 then(CustomController... handlers) {
        Collections.addAll(list(), handlers);
        return this;
    }

    public Handlers3 then(Handlers3 handlers) {
        with(handlers.args());
        exceptionHandler(handlers.exceptionHandler());
        completeHandler(handlers.completeHandler());
        list().addAll(handlers.list());
        return this;
    }

    public Handler3 exceptionHandler() {
        return exceptionHandler;
    }

    public Handlers3 exceptionHandler(Handler3 exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public Handlers3 exceptionHandler(Handlers3 exceptionHandler) {
        this.exceptionHandler = exceptionHandler.handler();
        return this;
    }

    public Handler3 completeHandler() {
        return completeHandler;
    }

    public Handlers3 completeHandler(Handler3 completeHandler) {
        this.completeHandler = completeHandler;
        return this;
    }

    public Handlers3 completeHandler(Handlers3 completeHandler) {
        this.completeHandler = completeHandler.handler();
        return this;
    }

    public List<CustomController> list() {
        if (controllers == null) {
            controllers = new ArrayList<>();
        }
        return controllers;
    }

    public Handlers3 with(Supplier<E3> argsConstructor) {
        this.argsConstructor = argsConstructor;
        return this;
    }

    public Supplier<E3> args() {
        return argsConstructor;
    }

    public void handle(E1 event1, E2 event2) {
        handle(event1, event2, null);
    }

    public void handle(E1 event1, E2 event2, E3 arguments) {
        if (handler == null) {
            handler = handler();
        }
        handler.handle(event1, event2, arguments);
    }

    public Handler3 handler() {
        return (event1, event2, event3) -> {
            Handler3 complete;
            if (completeHandler() == null) {
                complete = (e1, e2, e3) -> {
                };
            } else {
                complete = completeHandler::handle;
            }
            Handler3 fail;
            if (exceptionHandler() == null) {
                fail = (e1, e2, e3) -> {
                };
            } else {
                fail = exceptionHandler::handle;
            }
            Handler3 last = (e1, e2, e3) -> {
            };
            final AtomicBoolean stop = new AtomicBoolean(false);
            for (int i = list().size() - 1; i >= 0; i--) {
                final Handler3 previous = last;
                last = list().get(i).handle(e3 -> {
                    stop.set(true);
                    fail.handle(event1, event2, e3);
                }, e3 -> {
                    if (!stop.get()) previous.handle(event1, event2, e3);
                });
            }
            last.handle(event1, event2, event3);
            complete.handle(event1, event2, event3);
        };
    }

    public static Handlers3 merge(Handlers3 handler1, Handlers3 handler2) {
        handler2.with(handler1.args());
        handler2.exceptionHandler(handler1.exceptionHandler());
        handler2.completeHandler(handler1.completeHandler());
        int index = 0;
        for (Object o : handler1.list()) {
            handler2.list().add(index++, o);
        }
        return handler2;
    }

    public static Handlers3 merge(Handlers3 handler1, Handlers3 handler2, Handlers3 merged) {
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
