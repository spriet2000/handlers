package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Handlers<E, A>  {

    private List<BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, A>>> handlers;

    private BiConsumer<E, A> handler;

    @SafeVarargs
    public Handlers(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, A>>... handlers) {
        this.handlers = new ArrayList<>();
        for (BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, A>> handler : handlers) {
            this.handlers.add(handler);
        }
    }

    public void accept(E event, Object event2, BiConsumer<E, Throwable> exceptionHandler, BiConsumer<E, A> successHandler) {
        if (handler == null){
            handler = handler(exceptionHandler, successHandler);
        }
        handler.accept(event, (A)event2);
    }

    public BiConsumer<E, A> handler(BiConsumer<E, Throwable> exceptionHandler, BiConsumer<E, A> successHandler) {
        return (event1, event2) -> {
            BiConsumer<E, A> last = successHandler::accept;
            for (int i = handlers().size() - 1; i >= 0; i--) {
                final BiConsumer<E, A> previous = last;
                last = handlers().get(i).apply(
                        e2 -> exceptionHandler.accept(event1, e2),
                        e2 -> previous.accept(event1, (A) e2));
            }
            last.accept(event1, event2);
        };
    }

    @SafeVarargs
    public final Handlers<E, A> andThen(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, A>>... handlers) {
        if (this.handlers == null){
            this.handlers = new ArrayList<>();
        }
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    public List<BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, A>>> handlers() {
        if (handlers == null) {
            handlers = new ArrayList<>();
        }
        return handlers;
    }
}
