package me.yakar.mizan.platform.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.json.JavalinJackson;
import java.util.List;
import java.util.function.Consumer;

public class HttpServer implements AutoCloseable {

  private final Javalin http;

  private HttpServer(Javalin http) {
    this.http = http;
  }

  public static HttpServer start(List<Consumer<JavalinConfig>> endpoints, int port) {
    ProblemDetailHandlers problems = new ProblemDetailHandlers();
    Javalin http =
        Javalin.create(
                configuration -> {
                  configuration.jsonMapper(
                      new JavalinJackson().updateMapper(HttpServer::configure));
                  problems.register(configuration);
                  endpoints.forEach(endpoint -> endpoint.accept(configuration));
                })
            .start(port);
    return new HttpServer(http);
  }

  private static void configure(ObjectMapper mapper) {
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Override
  public void close() {
    http.stop();
  }
}
