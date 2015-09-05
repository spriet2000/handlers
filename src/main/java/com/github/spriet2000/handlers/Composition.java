package com.github.spriet2000.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;


public class Composition<E> {

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
        BiConsumer last = successHandler::accept;
        for (int i = handlers.size() - 1; i >= 0; i--) {
            final BiConsumer previous = last;
            last = handlers.get(i).handler(
                    (e, a) -> exceptionHandler.accept(event, a),
                    (e, a) -> previous.accept(event, a));
        }
        last.accept(event, event2);
    }
}
