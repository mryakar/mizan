package me.yakar.mizan.platform.http;

import com.fasterxml.jackson.annotation.JsonInclude;

/** RFC 7807 problem details. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetail(String type, String title, int status, String detail, String field) {

  public static ProblemDetail invalidRequest(String detail, String field) {
    return new ProblemDetail(
        "https://mizan.yakar.me/problems/invalid-request", "Invalid request", 400, detail, field);
  }

  public static ProblemDetail notFound(String detail) {
    return new ProblemDetail(
        "https://mizan.yakar.me/problems/not-found", "Not found", 404, detail, null);
  }

  public static ProblemDetail internalError() {
    return new ProblemDetail(
        "https://mizan.yakar.me/problems/internal-error",
        "Internal server error",
        500,
        "The request could not be completed.",
        null);
  }
}
