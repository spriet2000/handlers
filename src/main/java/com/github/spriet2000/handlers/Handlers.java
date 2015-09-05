package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Handlers<E, A> {

    private List<BiFunction<Consumer<Throwable>, Consumer<E>, BiConsumer<E, A>>> handlers;

    @SafeVarargs
    public Handlers(BiFunction<Consumer<Throwable>, Consumer<E>, BiConsumer<E, A>>... handlers) {
        this.handlers = new ArrayList<>();
        Collections.addAll(this.handlers, handlers);
    }

    @SafeVarargs
    public static <E, A> Handlers<E, A> compose(BiFunction<Consumer<Throwable>, Consumer<Object>, BiConsumer<E, A>>... handlers) {
        return new Handlers(handlers);
    }

    @SafeVarargs
    public static <E> Composition<E> compose(Handlers... handlers) {
        return new Composition(handlers);
    }

    public BiConsumer apply(BiConsumer<E, Throwable> exceptionHandler, BiConsumer<E, A> successHandler) {
        return (event1, event2) -> {
            BiConsumer<E, A> last = successHandler::accept;
            for (int i = handlers.size() - 1; i >= 0; i--) {
                final BiConsumer previous = last;
                last = handlers.get(i).apply(
                        e2 -> exceptionHandler.accept((E) event1, e2),
                        e2 -> previous.accept(event1, e2));
            }
            last.accept((E) event1, (A) event2);
        };
    }
}
handlers.apply(exception, success).accept(null, null)
    @SafeVarargs
    public final Handlers<E, A> andThen(BiFunction<Consumer<Throwable>, Consumer<E>, BiConsumer<E, A>>... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    public static class Composition<E> {

        private BiConsumer handler;
        private List<Handlers> handlers;
        private BiConsumer exceptionHandler = (e, a) -> {} ;
        private BiConsumer successHandler= (e, a) -> {} ;

        public Composition(Handlers... handlers) {
            this.handlers = new ArrayList<>();
            Collections.addAll(this.handlers, handlers);
        }

        public Composition<E> andThen(Handlers... handlers) {
            Collections.addAll(this.handlers, handlers);
            return this;
        }

        public Composition<E> exceptionHandler(BiConsumer<E, Throwable> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public Composition<E> successHandler(BiConsumer<E, Object> successHandler) {
            this.successHandler = successHandler;
            return this;
        }

        public void accept(E event, Object event2) {
            if (handler == null){
                handler = handler();
            }
            handler.accept(event, event2);
        }

        private BiConsumer handler(){
            return (event, arg) -> {
                BiConsumer last = successHandler::accept;
                for (int i = handlers.size() - 1; i >= 0; i--) {
                    final BiConsumer previous = last;
                    last = handlers.get(i).apply(
                            (e, a) -> exceptionHandler.accept(event, a),
                            (e, a) -> previous.accept(event, a));
                }
                last.accept(event, arg);
            };
        }
    }
}
