package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Handlers<E>  {

    private List<BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>> handlers;

    private BiConsumer<E, Object> handler;
    private BiConsumer<Object, Throwable> exceptionHandler;
    private BiConsumer<Object, Object> successHandler;

    public Handlers(BiConsumer<Object, Throwable> exceptionHandler, BiConsumer<Object, Object> successHandler) {
        this.exceptionHandler = exceptionHandler;
        this.successHandler = successHandler;
    }

    @SafeVarargs
    public Handlers(BiConsumer<Object, Throwable> exceptionHandler, BiConsumer<Object, Object> successHandler,
                    BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>... handlers) {
        this.handlers = new ArrayList<>();
        for (BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>> handler : handlers) {
            this.handlers.add(handler);
        }
        this.exceptionHandler = exceptionHandler;
        this.successHandler = successHandler;
    }

    public void accept(E event, Object event2) {
        if (handler == null){
            handler = handler();
        }
        handler.accept(event, event2);
    }

    public BiConsumer<E, Object> handler() {
        return (event1, event2) -> {
            BiConsumer<E, Object> last = successHandler::accept;
            for (int i = handlers().size() - 1; i >= 0; i--) {
                final BiConsumer<E, Object> previous = last;
                last = handlers().get(i).apply(
                        e2 -> exceptionHandler.accept(event1, e2),
                        e2 -> previous.accept(event1, e2));
            }
            last.accept(event1, event2);
        };
    }

    @SafeVarargs
    public final Handlers<E> andThen(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>... handlers) {
        if (this.handlers == null){
            this.handlers = new ArrayList<>();
        }
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    public List<BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>> handlers() {
        if (handlers == null) {
            handlers = new ArrayList<>();
        }
        return handlers;
    }
}
