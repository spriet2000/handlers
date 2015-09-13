package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class Handlers<E, A> {

    private List<BiFunction<Consumer<Throwable>, Consumer<A>, BiConsumer<E, A>>> handlers;

    @SafeVarargs
    public Handlers(BiFunction<Consumer<Throwable>, Consumer<A>, BiConsumer<E, A>>... handlers) {
        this.handlers = new ArrayList<>();
        Collections.addAll(this.handlers, handlers);
    }

    @SafeVarargs
    public Handlers(Handlers<E, A>... handlers) {
        this.handlers = new ArrayList<>();
        for (Handlers<E, A> handler : handlers) {
            this.handlers.addAll(handler.handlers.stream().collect(Collectors.toList()));
        }
    }

    @SafeVarargs
    public static <E, A> Handlers<E, A> compose(Handlers<E, A>... handlers) {
        return new Handlers<>(handlers);
    }

    @SafeVarargs
    public static <E, A> Handlers<E, A> compose(BiFunction<Consumer<Throwable>, Consumer<A>, BiConsumer<E, A>>... handlers) {
        return new Handlers<>(handlers);
    }

    public BiConsumer<E, A> apply(BiConsumer<E, Throwable> exceptionHandler, BiConsumer<E, A> successHandler) {
        return (event, argument) -> {
            BiConsumer<E, A> last = successHandler::accept;
            for (int i = handlers.size() - 1; i >= 0; i--) {
                final BiConsumer<E, A> previous = last;
                last = handlers.get(i).apply(
                        a -> exceptionHandler.accept(event, a),
                        a -> previous.accept(event, a));
            }
            last.accept(event, argument);
        };
    }

    @SafeVarargs
    public final Handlers<E, A> andThen(BiFunction<Consumer<Throwable>, Consumer<A>, BiConsumer<E, A>>... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    @SafeVarargs
    public final Handlers<E, A> andThen(Handlers<E, A>... handlers) {
        for (Handlers<E, A> handler : handlers) {
            this.handlers.addAll(handler.handlers.stream().collect(Collectors.toList()));
        }
        return this;
    }
}
