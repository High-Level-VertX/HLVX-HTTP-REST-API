package com.github.hlvx.rest.resources;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.writer.HttpResponseWriter;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/swagger-docs")
@Produces("application/json;charset=UTF-8")
public class SwaggerResource {
    private final OpenAPI openAPI;

    public SwaggerResource(OpenAPI openAPI) {
        this.openAPI = openAPI;
    }

    @GET
    @ResponseWriter(Writer.class)
    public OpenAPI get() {
        return openAPI;
    }

    public static class Writer implements HttpResponseWriter<OpenAPI> {

        @Override
        public void write(OpenAPI openAPI,
                          HttpServerRequest httpServerRequest,
                          HttpServerResponse httpServerResponse) throws Throwable {
            httpServerResponse.setStatusCode(200);
            httpServerResponse.end(Json.pretty(openAPI));
        }
    }
}
