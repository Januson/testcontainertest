package wiremock;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final TestAppContainer app;

    public TestClient(TestAppContainer app) {
        this.app = app;
    }

    public String testCall() {
        try {
            return doCall();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String doCall() throws IOException, InterruptedException {
        var url = "http://localhost:" + app.getMappedPort(8080);
        var request =
            HttpRequest
                .newBuilder()
                .uri(URI.create(url + "/result"))
                .header("Content-Type", "text/plain")
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }
}
