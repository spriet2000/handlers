package com.github.spriet2000.vertx.handlers;


import io.vertx.core.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Handlers<T> implements Handler<T> {

    private List<BiFunction<Handler<Throwable>, Handler<Object>,Handler<T>>> handlers = new ArrayList<>();

    private Handler<T> handler;

    private Handler<Throwable> exception;
    private Handler<Object> succeeded;

    @SafeVarargs
    public Handlers(Handler<Throwable> exception, Handler<Object> succeeded, BiFunction<Handler<Throwable>, Handler<Object>, Handler<T>>... handlers){
        for (BiFunction<Handler<Throwable>, Handler<Object>, Handler<T>> handler : handlers) {
            this.handlers.add(handler);
        }
        this.exception= exception;
        this.succeeded = succeeded;
    }

    @Override
    public void handle(T event) {
        if (handler == null) {
            handler  = handler();
        }
        handler.handle(event);
    }

    public Handlers<T> then(BiFunction<Handler<Throwable>, Handler<Object>, Handler<T>> handler){
        this.handlers.add(handler);
        return this;
    }

    @SafeVarargs
    public final Handlers<T> then(BiFunction<Handler<Throwable>, Handler<Object>, Handler<T>>... handlers){
        for (BiFunction<Handler<Throwable>, Handler<Object>, Handler<T>> handler : handlers) {
            this.handlers.add(handler);
        }
        return this;
    }

    public Handler<T> handler(){
        handler  = handlers.get(handlers.size() - 1).apply(exception, succeeded);
        for (int i = handlers.size() - 2; i >= 0; i--) {
            handler = handlers.get(i).apply(exception, (Handler<Object>) handler);
        }
        return handler;
    }
}
