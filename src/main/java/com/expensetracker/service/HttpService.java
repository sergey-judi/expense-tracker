package com.expensetracker.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class HttpService<T> {

  private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

  private final ObjectMapper objectMapper;

  @SneakyThrows
  public<E> E get(String uri, Class<E> clazz){
    HttpRequest request = HttpRequest
        .newBuilder(URI.create(uri))
        .header("Accept", "application/json")
        .GET()
        .build();

    HttpResponse<String> responseJson = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return objectMapper.readValue(responseJson.body(), clazz);
  }

  @SneakyThrows
  public T post(T body, String uri) {
    HttpRequest request = HttpRequest
        .newBuilder(URI.create(uri))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(serialize(body)))
        .build();

    HttpResponse<String> responseJson = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return objectMapper.readValue(responseJson.body(), new TypeReference<>() {});
  }

  @SneakyThrows
  public T put(int id, T body, String uri) {
    HttpRequest request = HttpRequest
        .newBuilder(URI.create(uri + "/" + id))
        .header("Accept", "application/json")
        .header("Content-Type", "application/json")
        .PUT(HttpRequest.BodyPublishers.ofString(serialize(body)))
        .build();

    HttpResponse<String> responseJson = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    return objectMapper.readValue(responseJson.body(), new TypeReference<>() {});
  }

  @SneakyThrows
  public void delete(int id, String uri) {
    HttpRequest request = HttpRequest
        .newBuilder(URI.create(uri + "/" + id))
        .DELETE()
        .build();

    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  @SneakyThrows
  public String serialize(Object object) {
    return objectMapper.writeValueAsString(object);
  }

}
