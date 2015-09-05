package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Handlers<E, A> {

    private List<BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, A>>> handlers;

    @SafeVarargs
    public Handlers(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, A>>... handlers) {
        this.handlers = new ArrayList<>();
        Collections.addAll(this.handlers, handlers);
    }

    @SafeVarargs
    public static <E, A> Handlers<E, A> compose(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, A>>... handlers) {
        return new Handlers<>(handlers);
    }

    @SafeVarargs
    public static Composition compose(Handlers... handlers) {
        return new Composition(handlers);
    }

    public BiConsumer handler(BiConsumer<Object, Throwable> exceptionHandler, BiConsumer<E, A> successHandler) {
        return (event1, event2) -> {
            BiConsumer<E, A> last = successHandler::accept;
            for (int i = handlers.size() - 1; i >= 0; i--) {
                final BiConsumer previous = last;
                last = handlers.get(i).apply(
                        e2 -> exceptionHandler.accept(event1, e2),
                        e2 -> previous.accept(event1, e2));
            }
            last.accept((E) event1, (A) event2);
        };
    }

    @SafeVarargs
    public final Handlers<E, A> andThen(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, A>>... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }
}
