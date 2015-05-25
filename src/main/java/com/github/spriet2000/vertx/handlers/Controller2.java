package com.github.spriet2000.vertx.handlers;

import io.vertx.core.Handler;

public interface Controller2<EventHandler extends Handler2> {

    EventHandler handle(Handler<Object> fail, Handler<Object> next);
}
