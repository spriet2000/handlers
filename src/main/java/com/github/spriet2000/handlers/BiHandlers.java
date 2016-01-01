package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public final class BiHandlers<E, A> {

    private List<BiFunction<BiConsumer<E, Throwable>, BiConsumer<E, A>, BiConsumer<E, A>>> handlers = new ArrayList<>();

    BiConsumer<E, A> cache;

    public BiHandlers() {
    }

    @SafeVarargs
    public BiHandlers(BiFunction<BiConsumer<E, Throwable>, BiConsumer<E, A>, BiConsumer<E, A>>... handlers) {
        Collections.addAll(this.handlers, handlers);
    }

    @SafeVarargs
    public BiHandlers(BiHandlers<E, A>... handlers) {
        for (BiHandlers<E, A> handler : handlers) {
            this.handlers.addAll(handler.handlers.stream().collect(Collectors.toList()));
        }
    }

    @SafeVarargs
    public static <E, A> BiHandlers<E, A> compose(BiHandlers<E, A>... handlers) {
        return new BiHandlers<>(handlers);
    }

    @SafeVarargs
    public static <E, A> BiHandlers<E, A> compose(BiFunction<BiConsumer<E, Throwable>, BiConsumer<E, A>, BiConsumer<E, A>>... handlers) {
        return new BiHandlers<>(handlers);
    }

    public BiConsumer<E, A> apply(BiConsumer<E, Throwable> exceptionHandler, BiConsumer<E, A> successHandler) {
        BiConsumer<E, A> last = successHandler::accept;
        for (int i = handlers.size() - 1; i >= 0; i--) {
            final BiConsumer<E, A> previous = last;
            last = handlers.get(i).apply(
                    exceptionHandler::accept,
                    previous::accept);
        }
        return last::accept;
    }

    @SafeVarargs
    public final BiHandlers<E, A> andThen(BiConsumer<E, A>... consumers) {
        for (BiConsumer<E, A> consumer : consumers) {
            handlers.add((f, n) -> consumer::accept);
        }
        return this;
    }

    @SafeVarargs
    public final BiHandlers<E, A> andThen(BiFunction<BiConsumer<E, Throwable>, BiConsumer<E, A>, BiConsumer<E, A>>... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    @SafeVarargs
    public final BiHandlers<E, A> andThen(BiHandlers<E, A>... handlers) {
        for (BiHandlers<E, A> handler : handlers) {
            this.handlers.addAll(handler.handlers.stream().collect(Collectors.toList()));
        }
        return this;
    }
}

