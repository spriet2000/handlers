package com.github.spriet2000.vertx.handlers;

public interface Handler2<E1, E2> {

    void handle(E1 event1, E2 event2);
}
