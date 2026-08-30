package me.yakar.mizan.platform.http;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import me.yakar.mizan.shared.error.NotFoundException;
import me.yakar.mizan.shared.error.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProblemDetailHandlers {

  private static final Logger LOG = LoggerFactory.getLogger(ProblemDetailHandlers.class);
  private static final String PROBLEM_JSON = "application/problem+json";

  public void register(JavalinConfig configuration) {
    configuration.routes.exception(ValidationException.class, this::invalidRequest);
    configuration.routes.exception(NotFoundException.class, this::notFound);
    configuration.routes.exception(Exception.class, this::unexpected);
  }

  void invalidRequest(ValidationException failure, Context context) {
    respond(context, ProblemDetail.invalidRequest(failure.getMessage(), failure.field()));
  }

  void notFound(NotFoundException failure, Context context) {
    respond(context, ProblemDetail.notFound(failure.getMessage()));
  }

  void unexpected(Exception failure, Context context) {
    LOG.error("Unhandled failure while serving {} {}", context.method(), context.path(), failure);
    respond(context, ProblemDetail.internalError());
  }

  private void respond(Context context, ProblemDetail problem) {
    context.status(problem.status()).json(problem).contentType(PROBLEM_JSON);
  }
}
