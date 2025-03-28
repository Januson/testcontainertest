package org.test.testcontainers;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller("/result")
public class ResultController {

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String getResult() throws IOException, InterruptedException {
        String testHost = System.getenv("TEST_HOST");
        if (testHost == null || testHost.isBlank()) {
            throw new IllegalArgumentException("TEST_HOST is not set");
        }
        return getMappings(testHost);
    }

    public String getMappings(String testHost) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(testHost + "/__admin/mappings"))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("Failed to get mappings: HTTP " + response.statusCode());
        }
    }
}