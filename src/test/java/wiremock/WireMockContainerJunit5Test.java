package wiremock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class WireMockContainerJunit5Test {

    private static final Logger LOG = LoggerFactory.getLogger("logz");
    private static final Slf4jLogConsumer CONTAINER_LOG_CONSUMER = new Slf4jLogConsumer(LOG);

    private static final Network NETWORK = Network.newNetwork();
    TestServiceContainer service;
    TestAppContainer app;

    @BeforeEach
    void setUp() {
        service = new TestServiceContainer(NETWORK);
        service.start();
        app = new TestAppContainer(NETWORK, service);
        app.start();
        app.followOutput(CONTAINER_LOG_CONSUMER);

        waitForWireMock();

        String template = """
            BaseURL: %s
            Port mapping: 8080 -> %s
            First port: %s
            %n""";
        System.out.printf(
            template,
            service.baseUrl(),
            service.getMappedPort(8080),
            service.getFirstMappedPort()
        );
    }

    @Test
    void helloWorld() {
        String body = "{\"accessRights\":{\"read\":true,\"write\":true}}";
        var stubber = new TestStubber(service);
        TestClient client = new TestClient(service);
        stubber.stubTestEndpoint(body);

        String response = client.testCall();

        assertThat(response).isEqualTo(body);
    }

    private void waitForWireMock() {
        var url = service.baseUrl();
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
