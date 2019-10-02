package com.github.hlvx.rest.authentication;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.web.RoutingContext;

public interface AuthProvider {
    void authorize(RoutingContext context, Handler<AsyncResult<AbstractUser>> handler);
}
