package com.github.spriet2000.vertx.handlers;

import io.vertx.core.Handler;

import java.util.List;
import java.util.function.BiFunction;

public interface Handleable<T> {

    Handler<T> handler();
    List<BiFunction<Handler<Throwable>, Handler<Object>,Handler<T>>> handlers();
    Handler<Throwable> exceptionHandler();
    Handler<Object> successHandler();
}
