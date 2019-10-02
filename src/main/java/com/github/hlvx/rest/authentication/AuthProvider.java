package com.github.hlvx.rest.authentication;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

/**
 * Interface used to create and manage Authentication processes
 * @author AlexMog
 */
public interface AuthProvider {
    /**
     * Called on each request.
     * {@link RoutingContext#setUser(User)} muse be used to set the authenticated user.
     * DO NOT USE {@link RoutingContext#next()} IN THIS METHOD. It will be called automatically.
     * @param context The context of the request
     * @param handler The handler to call when the authentication process is done
     */
    void authorize(RoutingContext context, Handler<AsyncResult<AbstractUser>> handler);
}
