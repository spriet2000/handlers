package com.github.spriet2000.vertx.handlers;

import io.vertx.core.Handler;

public interface Controller<EventHandler extends Handler> {

    EventHandler handle(Handler<Object> fail, Handler<Object> next);
}
