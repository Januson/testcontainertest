package wiremock;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestStubber {
    private static final String ACCESS_RIGHTS_BODY_TEMPLATE =
        """
            {
              "request": {
                "method": "GET",
                "url": "%s"
              },
              "response": {
                "status": 200,
                "headers": {
                    "Content-Type": "application/json"
                },
                "jsonBody": %s
              }
            }
            """;

    private final HttpClient client = HttpClient.newHttpClient();
    private final TestServiceContainer wiremockServer;

    public TestStubber(TestServiceContainer wiremockServer) {
        this.wiremockServer = wiremockServer;
    }

    public void stubTestEndpoint(String body) {
        var endpoint = "/api/v2/entity/access";
        var requestBody = ACCESS_RIGHTS_BODY_TEMPLATE.formatted(endpoint, body);

        try {
            createWireMockMapping(requestBody);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void createWireMockMapping(String requestBody) throws IOException, InterruptedException {
        var url = wiremockServer.baseUrl();
        var request =
            HttpRequest
                .newBuilder()
                .uri(URI.create(url + "/__admin/mappings"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
