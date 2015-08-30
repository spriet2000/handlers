package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Handlers<E>  {

    private List<BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>> consumers;

    private BiConsumer<E, Object> handler;
    private BiConsumer<E, Throwable> exceptionHandler;
    private BiConsumer<E, Object> successHandler;

    public Handlers(Handlers<E> handlers) {
        exceptionHandler = handlers.exceptionHandler();
        successHandler = handlers.successHandler();
        this.consumers = handlers.consumers();
    }

    @SafeVarargs
    public Handlers(BiConsumer<E, Throwable> exceptionHandler, BiConsumer<E, Object> successHandler,
                    BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>... handlers) {
        this.consumers = new ArrayList<>();
        for (BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>> handler : handlers) {
            this.consumers.add(handler);
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
            for (int i = consumers().size() - 1; i >= 0; i--) {
                final BiConsumer<E, Object> previous = last;
                last = consumers().get(i).apply(
                        e2 -> exceptionHandler.accept(event1, e2),
                        e2 -> previous.accept(event1, e2));
            }
            last.accept(event1, event2);
        };
    }

    public Handlers<E> andThen(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>> handler) {
        this.consumers.add(handler);
        return this;
    }

    public List<BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>> consumers() {
        if (consumers == null) {
            consumers = new ArrayList<>();
        }
        return consumers;
    }

    public BiConsumer<E, Throwable> exceptionHandler() {
        return exceptionHandler;
    }

    public BiConsumer<E, Object> successHandler() {
        return successHandler;
    }
}
