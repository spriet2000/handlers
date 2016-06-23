package com.github.spriet2000.railways;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class Railway<A> {

    private final List<BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>> handlers = new ArrayList<>();

    @SafeVarargs
    public Railway(BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>... handlers) {
        Collections.addAll(this.handlers, handlers);
    }

    @SafeVarargs
    public Railway(Railway<A>... handlers) {
        for (Railway<A> handler : handlers) {
            this.handlers.addAll(handler.handlers.stream().collect(Collectors.toList()));
        }
    }

    public Consumer<A> apply(Consumer<Throwable> exceptionHandler, Consumer<A> successHandler) {
        Consumer<A> last = successHandler;
        for (int i = handlers.size() - 1; i >= 0; i--) {
            final Consumer<A> previous = last;
            last = handlers.get(i).apply(
                    exceptionHandler,
                    previous);
        }
        return last;
    }

    @SafeVarargs
    public final Railway<A> andThen(Consumer<A>... consumers) {
        for (Consumer<A> consumer : consumers) {
            handlers.add((f, n) -> consumer);
        }
        return this;
    }

    @SafeVarargs
    public final Railway<A> andThen(BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    @SafeVarargs
    public final Railway<A> andThen(Railway<A>... handlers) {
        for (Railway<A> handler : handlers) {
            this.handlers.addAll(handler.handlers.stream().collect(Collectors.toList()));
        }
        return this;
    }


}

