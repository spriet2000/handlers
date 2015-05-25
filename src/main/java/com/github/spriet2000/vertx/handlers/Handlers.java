package com.github.spriet2000.vertx.handlers;

import io.vertx.core.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unchecked")
public class Handlers<E1,
        CustomHandler extends Handler<E1>,
        CustomController extends Controller<CustomHandler>> implements Handler<E1> {

    private List<CustomController> controllers;
    private Handler exceptionHandler;
    private Handler completeHandler;
    private Handler handler;

    public Handlers() { }

    public Handlers(Handlers controllers) {
        then(controllers);
    }

    public Handlers(CustomHandler... controllers) {
        then(controllers);
    }

    public Handlers(CustomController... controllers) {
        then(controllers);
    }

    public Handlers then(CustomHandler... handlers) {
        for (Handler<E1> handler : handlers) {
            list().add((CustomController) (Controller) (fail, next) -> (e) -> {
                handler.handle((E1) e);
                next.handle(null);
            });
        }
        return this;
    }

    public Handlers then(CustomController... handlers) {
        Collections.addAll(list(), handlers);
        return this;
    }

    public Handlers then(Handlers handlers) {
        exceptionHandler((CustomHandler)handlers.exceptionHandler());
        completeHandler((CustomHandler)handlers.completeHandler());
        list().addAll(handlers.list());
        return this;
    }

    protected Handler exceptionHandler() {
        return exceptionHandler;
    }

    public Handlers exceptionHandler(CustomHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public Handlers exceptionHandler(Handlers exceptionHandler) {
        this.exceptionHandler = exceptionHandler.handler();
        return this;
    }

    protected Handler completeHandler() {
        return completeHandler;
    }

    public Handlers completeHandler(CustomHandler completeHandler) {
        this.completeHandler = completeHandler;
        return this;
    }

    public Handlers completeHandler(Handlers completeHandler) {
        this.completeHandler = completeHandler.handler();
        return this;
    }

    public List<CustomController> list() {
        if (controllers == null) {
            controllers = new ArrayList<>();
        }
        return controllers;
    }

    public void handle(E1 event1) {
        if (handler == null) {
            handler = handler();
        }
        handler.handle(event1);
    }

    public Handler handler() {
        return (event1) -> {
            Handler complete;
            if (completeHandler() == null) {
                complete = (e1) -> {
                };
            } else {
                complete = completeHandler::handle;
            }
            Handler fail;
            if (exceptionHandler() == null) {
                fail = (e1) -> {
                };
            } else {
                fail = exceptionHandler::handle;
            }
            Handler last = (e1) -> {
            };
            final AtomicBoolean stop = new AtomicBoolean(false);
            for (int i = list().size() - 1; i >= 0; i--) {
                final Handler previous = last;
                last = list().get(i).handle(e3 -> {
                    stop.set(true);
                    fail.handle(e3);
                }, e3 -> {
                    if (!stop.get()) previous.handle(e3);
                });
            }
            last.handle(event1);
            complete.handle(event1);
        };
    }

    public static Handlers merge(Handlers handler1, Handlers handler2) {
        handler2.exceptionHandler(handler1.exceptionHandler());
        handler2.completeHandler(handler1.completeHandler());
        int index = 0;
        for (Object o : handler1.list()) {
            handler2.list().add(index++, o);
        }
        return handler2;
    }

    public static Handlers merge(Handlers handler1, Handlers handler2, Handlers merged) {
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
