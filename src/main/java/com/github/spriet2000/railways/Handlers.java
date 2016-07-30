package com.github.spriet2000.railways;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class Handlers<A> {

    private final List<BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>> methods = new ArrayList<>();

    @SafeVarargs
    public Handlers(BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>... methods) {
        Collections.addAll(this.methods, methods);
    }

    @SafeVarargs
    public Handlers(Handlers<A>... handlerses) {
        for (Handlers<A> handlers : handlerses) {
            this.methods.addAll(handlers.methods.stream().collect(Collectors.toList()));
        }
    }

    public Consumer<A> apply(Consumer<Throwable> stop, Consumer<A> next) {
        Consumer<A> last = next;
        for (int i = methods.size() - 1; i >= 0; i--) {
            final Consumer<A> previous = last;
            last = methods.get(i).apply(
                    stop,
                    previous);
        }
        return last;
    }

    @SafeVarargs
    public final Handlers<A> andThen(Consumer<A>... consumers) {
        for (Consumer<A> consumer : consumers) {
            methods.add((f, n) -> consumer);
        }
        return this;
    }

    @SafeVarargs
    public final Handlers<A> andThen(BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>... methods) {
        Collections.addAll(this.methods, methods);
        return this;
    }

    @SafeVarargs
    public final Handlers<A> andThen(Handlers<A>... handlerses) {
        for (Handlers<A> handlers : handlerses) {
            this.methods.addAll(handlers.methods.stream().collect(Collectors.toList()));
        }
        return this;
    }

    @SafeVarargs
    public static <A> Handlers<A> build(Handlers<A>... handlerses) {
        return new Handlers<>(handlerses);
    }

    @SafeVarargs
    public static <A> Handlers<A> build(BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>... methods) {
        return new Handlers<>(methods);
    }

}

