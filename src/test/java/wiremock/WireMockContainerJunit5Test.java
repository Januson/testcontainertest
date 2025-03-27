package wiremock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class WireMockContainerJunit5Test {

    TestServiceContainer wiremockServer;

    @BeforeEach
    void setUp() {
        wiremockServer = new TestServiceContainer();
        wiremockServer.start();

        waitForWireMock();

        String template = """
            BaseURL: %s
            Port mapping: 8080 -> %s
            First port: %s
            %n""";
        System.out.printf(
            template,
            wiremockServer.baseUrl(),
            wiremockServer.getMappedPort(8080),
            wiremockServer.getFirstMappedPort()
        );
    }

    @Test
    void helloWorld() {
        String body = "{\"accessRights\":{\"read\":true,\"write\":true}}";
        var stubber = new TestStubber(wiremockServer);
        TestClient client = new TestClient(wiremockServer);
        stubber.stubTestEndpoint(body);

        String response = client.testCall();

        assertThat(response).isEqualTo(body);
    }

    private void waitForWireMock() {
        var url = wiremockServer.baseUrl();
        var request = HttpRequest.newBuilder()
            .uri(URI.create(url + "/__admin/mappings"))
            .GET()
            .build();

        HttpClient client = HttpClient.newHttpClient();

        int retries = 10;
        for (int i = 0; i < retries; i++) {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return;
                }
            } catch (IOException | InterruptedException ignored) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
//        throw new RuntimeException("WireMock did not become available in time.");
        System.out.println("WireMock did not become available in time.");
    }
}
