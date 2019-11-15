package gov.nasa.jpl.ammos.mpsa.aerie.plan.utils;

import javax.json.bind.JsonbBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public final class HttpRequester {
  private final HttpClient client;
  private final URI baseUri;

  public HttpRequester(final HttpClient client, final URI baseUri) {
    this.client = client;
    this.baseUri = baseUri;
  }

  public HttpResponse<String> sendRequest(final String method, final String path)
      throws IOException, InterruptedException
  {
    return sendRequest(method, path, Optional.empty());
  }

  public <T> HttpResponse<String> sendRequest(final String method, final String path, T body)
      throws IOException, InterruptedException
  {
    return sendRequest(method, path, Optional.of(body));
  }

  public <T> HttpResponse<String> sendRequest(final String method, final String path, final Optional<T> body)
      throws IOException, InterruptedException
  {
    // TODO: Remove JSON-B -- probably just take an `Optional<JsonValue>` instead of an arbitrary `Optional<T>`.
    final HttpRequest.BodyPublisher bodyPublisher = body
        .map(x -> HttpRequest.BodyPublishers.ofString(JsonbBuilder.create().toJson(x)))
        .orElseGet(HttpRequest.BodyPublishers::noBody);

    final HttpRequest request = HttpRequest.newBuilder()
        .uri(baseUri.resolve(path))
        .method(method, bodyPublisher)
        .build();

    return this.client.send(request, HttpResponse.BodyHandlers.ofString());
  }
}
