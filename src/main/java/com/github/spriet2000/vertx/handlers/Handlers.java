package com.github.spriet2000.vertx.handlers;


import io.vertx.core.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public final class Handlers<E> implements Handler<E>, Handleable<E> {

    private List<BiFunction<Handler<Throwable>, Handler<Object>,Handler<E>>> handlers;

    private Handler<E> handler;
    private Handler<Throwable> exceptionHandler;
    private Handler<Object> successHandler;

    public Handlers(Handleable<E> handlers){
        exceptionHandler = handlers.exceptionHandler();
        successHandler = handlers.successHandler();
        this.handlers = handlers.handlers();
    }

    @SafeVarargs
    public Handlers(Handler<Throwable> exceptionHandler, Handler<Object> successHandler, BiFunction<Handler<Throwable>, Handler<Object>, Handler<E>>... handlers){
        this.handlers = new ArrayList<>();
        for (BiFunction<Handler<Throwable>, Handler<Object>, Handler<E>> handler : handlers) {
            this.handlers.add(handler);
        }
        this.exceptionHandler = exceptionHandler;
        this.successHandler = successHandler;
    }

    @Override
    public void handle(E event) {
        if (handler == null) {
            handler  = handler();
        }
        handler.handle(event);
    }

    public Handlers<E> then(BiFunction<Handler<Throwable>, Handler<Object>, Handler<E>> handler){
        this.handlers.add(handler);
        return this;
    }

    public Handler<E> handler(){
        handler  = handlers.get(handlers.size() - 1).apply(exceptionHandler, successHandler);
        for (int i = handlers.size() - 2; i >= 0; i--) {
            handler = handlers.get(i).apply(exceptionHandler, (Handler<Object>) handler);
        }
        return handler;
    }

    @Override
    public List<BiFunction<Handler<Throwable>, Handler<Object>, Handler<E>>> handlers() {
        if(handlers == null) {
            handlers  = new ArrayList<>();
        }
        return handlers;
    }

    @Override
    public Handler<Throwable> exceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public Handler<Object> successHandler() {
        return successHandler;
    }
}
