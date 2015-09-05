package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Handlers<E> {

    private List<BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>> handlers;

    @SafeVarargs
    public Handlers(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>... handlers) {
        this.handlers = new ArrayList<>();
        Collections.addAll(this.handlers, handlers);
    }

    @SafeVarargs
    public static <E> Handlers<E> compose(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>... handlers) {
        return new Handlers<>(handlers);
    }

    @SafeVarargs
    public static Composition compose(Handlers... handlers) {
        return new Composition(handlers);
    }

    public BiConsumer handler(BiConsumer<Object, Throwable> exceptionHandler, BiConsumer<E, Object> successHandler) {
        return (event1, event2) -> {
            BiConsumer<E, Object> last = successHandler::accept;
            for (int i = handlers.size() - 1; i >= 0; i--) {
                final BiConsumer previous = last;
                last = handlers.get(i).apply(
                        e2 -> exceptionHandler.accept(event1, e2),
                        e2 -> previous.accept(event1, e2));
            }
            last.accept((E) event1, event2);
        };
    }

    @SafeVarargs
    public final Handlers<E> andThen(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, Object>>... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }
}
