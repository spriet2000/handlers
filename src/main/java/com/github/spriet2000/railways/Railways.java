package com.github.spriet2000.railways;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Railways{

    @SafeVarargs
    public static <A> Railway<A> compose(Railway<A>... handlers) {
        return new Railway<>(handlers);
    }

    @SafeVarargs
    public static <A> Railway<A> compose(BiFunction<Consumer<Throwable>, Consumer<A>, Consumer<A>>... handlers) {
        return new Railway<>(handlers);
    }

}
