package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class Handlers<E, A> {

    private List<BiFunction<Consumer<Throwable>, Consumer<A>, BiConsumer<E, A>>> handlers;

    @SafeVarargs
    public Handlers(BiFunction<Consumer<Throwable>, Consumer<A>, BiConsumer<E, A>>... handlers) {
        this.handlers = new ArrayList<>();
        Collections.addAll(this.handlers, handlers);
    }

    @SafeVarargs
    public static <E, A> Handlers<E, A> compose(BiFunction<Consumer<Throwable>, Consumer<A>, BiConsumer<E, A>>... handlers) {
        return new Handlers(handlers);
    }

    @SafeVarargs
    public static <E, A> Composition<E, A> compose(Handlers<E, A>... handlers) {
        return new Composition<>(handlers);
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

    @SafeVarargs
    public final Handlers<E, A> andThen(BiFunction<Consumer<Throwable>, Consumer<A>, BiConsumer<E, A>>... handlers) {
        Collections.addAll(this.handlers, handlers);
        return this;
    }

    public static class Composition<E, A> {

        private BiConsumer<E, A> handler;
        private List<Handlers> handlers;
        private BiConsumer<E, Throwable>  exceptionHandler = (e, a) -> {} ;
        private BiConsumer<E, A>  successHandler= (e, a) -> {} ;

        public Composition(Handlers<E, A>... handlers) {
            this.handlers = new ArrayList<>();
            Collections.addAll(this.handlers, handlers);
        }

        public Composition<E, A> andThen(Handlers<E, A>... handlers) {
            Collections.addAll(this.handlers, handlers);
            return this;
        }

        public Composition<E, A> andThen(BiFunction<Consumer<Throwable>, Consumer<A>, BiConsumer<E, A>>... handlers) {
            this.handlers.add(new Handlers(handlers));
            return this;
        }

        public Composition<E, A> exceptionHandler(BiConsumer<E, Throwable> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public Composition<E, A> successHandler(BiConsumer<E, A> successHandler) {
            this.successHandler = successHandler;
            return this;
        }

        public void accept(E event, A event2) {
            if (handler == null){
                handler = handler();
            }
            handler.accept(event, event2);
        }

        private BiConsumer<E, A> handler(){
            return (event, arg) -> {
                BiConsumer<E, A> last = successHandler::accept;
                for (int i = handlers.size() - 1; i >= 0; i--) {
                    final BiConsumer previous = last;
                    last = handlers.get(i).apply(
                            (e, a) -> exceptionHandler.accept((E) e, (Throwable) a),
                            (e, a) -> previous.accept(e, a));
                }
                last.accept(event, arg);
            };
        }
    }
}
