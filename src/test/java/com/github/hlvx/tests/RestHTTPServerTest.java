package com.github.hlvx.tests;

import com.github.hlvx.rest.servers.RestHTTPServer;
import com.zandero.rest.RestRouter;
import com.zandero.rest.injection.InjectionProvider;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.annotation.security.RolesAllowed;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class RestHTTPServerTest {
    private static final int PORT = new Random().nextInt(42420) + 1000;
    private static final String HOST = "localhost";
    private static VertxTestContext vertxTestContext;
    private static Vertx vertx;
    private static WebClient client;
    private static RestHTTPServer httpServer;

    @BeforeAll
    public static void before() {
        vertx = Vertx.vertx();

        RestRouter.getReaders().clear();
        RestRouter.getWriters().clear();
        RestRouter.getExceptionHandlers().clear();
        RestRouter.getContextProviders().clear();

        RestRouter.validateWith((Validator) null);
        RestRouter.injectWith((InjectionProvider) null);

        client = WebClient.create(vertx);

        httpServer = new RestHTTPServer();
        httpServer
            .setServices(new TestService(), new TestService2())
            .setAuthenticationProvider((context, handler) -> {
                String token = context.request().getHeader("token");
                if (token != null) {
                    if (token.equals("error")) handler.handle(Future.failedFuture(new Exception("TestError")));
                    else handler.handle(Future.succeededFuture(new TestUser(token)));
                } else handler.handle(Future.succeededFuture(null));
            })
            .start(PORT, e -> {});
    }

    @Test
    public void simpleTest(VertxTestContext context) {
        client.get(PORT, HOST, "/test/simple")
                .as(BodyCodec.string())
                .send(httpResponse -> {
                    check(httpResponse);
                    assertEquals("application/json;charset=UTF-8", httpResponse.result().getHeader("content-type"));
                    assertEquals(204, httpResponse.result().statusCode());
                    context.completeNow();
                });
    }

    @Test
    public void queryParamTest(VertxTestContext context) {
        client.get(PORT, HOST, "/test/query_param")
                .addQueryParam("test", "hello")
                .as(BodyCodec.string())
                .send(httpResponse -> {
                    check(httpResponse);
                    assertEquals(httpResponse.result().body(), "\"hello\"");
                    context.completeNow();
                });
    }

    @Test
    public void pathParamTest(VertxTestContext context) {
        client.get(PORT, HOST, "/test/path_param/hello")
                .as(BodyCodec.string())
                .send(httpResponse -> {
                    check(httpResponse);
                    assertEquals(httpResponse.result().body(), "\"hello\"");
                    context.completeNow();
                });
    }

    @Test
    public void postParamTest(VertxTestContext context) {
        client.post(PORT, HOST, "/test/post_param")
                .as(BodyCodec.string())
                .sendForm(MultiMap.caseInsensitiveMultiMap().add("test", "hello"),
                        httpResponse -> {
                    check(httpResponse);
                    assertEquals(httpResponse.result().body(), "\"hello\"");
                    context.completeNow();
                });
    }

    @Test
    public void basicAuthTest(VertxTestContext context) {
        client.get(PORT, HOST, "/test/token")
                .putHeader("token", "hello")
                .as(BodyCodec.string())
                .send(httpResponse -> {
                    check(httpResponse);
                    assertEquals(httpResponse.result().body(), "\"hello\"");
                    context.completeNow();
                });
    }

    @Test
    public void rootPathTest(VertxTestContext context) {
        client.get(PORT, HOST, "/test2")
                .as(BodyCodec.string())
                .send(httpResponse -> {
                    check(httpResponse);
                    assertEquals(httpResponse.result().body(), "hello");
                    context.completeNow();
                });
    }

    private static void check(AsyncResult result) {
        if (result.failed()) throw new RuntimeException(result.cause());
    }

    @Path("test")
    @Produces("application/json;charset=UTF-8")
    public static class TestService {
        @GET
        @Path("simple")
        public void simpleTest(@Context HttpServerResponse response) {
            response.setStatusCode(204);
        }

        @GET
        @Path("query_param")
        public String queryParamTest(@QueryParam("test") String test) {
            return test;
        }

        @GET
        @Path("path_param/{test}")
        public String pathParamTest(@PathParam("test") String test) {
            return test;
        }

        @POST
        @Path("post_param")
        public String postParamTest(@FormParam("test") String test) {
            return test;
        }

        @GET
        @Path("token")
        @RolesAllowed("test")
        public String token(@Context User user) {
            return user.principal().getString("token");
        }
    }

    @Path("test2")
    public static class TestService2 {
        @GET
        public String rootPath() {
            return "hello";
        }
    }

    private static class TestUser extends AbstractUser {
        private String token;
        private JsonObject principal;

        public TestUser(String token) {
            this.token = token;
            principal = new JsonObject().put("token", token);
        }

        @Override
        protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
            resultHandler.handle(Future.succeededFuture(true));
        }

        @Override
        public JsonObject principal() {
            return principal;
        }

        @Override
        public void setAuthProvider(AuthProvider authProvider) {}
    }
}
