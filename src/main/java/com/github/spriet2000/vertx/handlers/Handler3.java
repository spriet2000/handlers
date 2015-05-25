package com.github.spriet2000.vertx.handlers;

public interface Handler3<E1, E2, E3> {

    void handle(E1 event1, E2 event2, E3 event3);
}
