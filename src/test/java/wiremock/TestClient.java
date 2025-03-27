package wiremock;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final TestServiceContainer wiremockServer;

    public TestClient(TestServiceContainer wiremockServer) {
        this.wiremockServer = wiremockServer;
    }

    public String testCall() {
        try {
            return doCall();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String doCall() throws IOException, InterruptedException {
        var url = wiremockServer.baseUrl();
        var request =
            HttpRequest
                .newBuilder()
                .uri(URI.create(url + "/api/v2/entity/access"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
