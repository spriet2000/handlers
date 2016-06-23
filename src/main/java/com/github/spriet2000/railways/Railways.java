package com.github.spriet2000.railways;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Railways{

    @SafeVarargs
    public static <A> Railway<A> build(Railway<A>... railways) {
        return new Railway<>(railways);
    }

    @SafeVarargs
    public static <A> Railway<A> build(BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>... methods) {
        return new Railway<>(methods);
    }

}
