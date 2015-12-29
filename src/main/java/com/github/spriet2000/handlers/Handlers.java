package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class Handlers<A> {

    private final List<BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>> handlers = new ArrayList<>();

    public Handlers() {
    }

    @SafeVarargs
    public Handlers(BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>... handlers) {
        Collections.addAll(this.handlers, handlers);
    }

    @SafeVarargs
    public Handlers(Handlers<A>... handlers) {
        for (Handlers<A> handler : handlers) {
            this.handlers.addAll(handler.handlers.stream().collect(Collectors.toList()));
        }
    }

    @SafeVarargs
    public static <A> Handlers<A> compose(Handlers<A>... handlers) {
        return new Handlers<>(handlers);
    }

    @SafeVarargs
    public static <A> Handlers<A> compose(BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>... handlers) {
        return new Handlers<>(handlers);
    }

    public Consumer<A> apply(Consumer<Throwable> exceptionHandler, Consumer<A> successHandler) {
        Consumer<A> last = successHandler::accept;
        for (int i = handlers.size() - 1; i >= 0; i--) {
            final Consumer<A> previous = last;
            last = handlers.get(i).apply(
                    exceptionHandler::accept,
                    previous::accept);
        }
        return last::accept;
    }

    @SafeVarargs
    public final Handlers<A> andThen(BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    @SafeVarargs
    public final Handlers<A> andThen(Handlers<A>... handlers) {
        for (Handlers<A> handler : handlers) {
            this.handlers.addAll(handler.handlers.stream().collect(Collectors.toList()));
        }
        return this;
    }
}

