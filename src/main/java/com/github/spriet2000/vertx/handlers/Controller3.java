package com.github.spriet2000.vertx.handlers;

import io.vertx.core.Handler;

public interface Controller3<EventHandler extends Handler3> {

    EventHandler handle(Handler<Object> fail, Handler<Object> next);
}
