package wiremock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class WireMockContainerJunit5Test {

    TestServiceContainer wiremockServer;

    @BeforeEach
    void setUp() {
        wiremockServer = new TestServiceContainer(Network.newNetwork());
        wiremockServer.start();

        String template = """
            BaseURL: %s
            MapperURL: %s
            Port: 8080 -> %s
            %n""";
        System.out.printf(
            template,
            wiremockServer.baseUrl(),
            wiremockServer.mappedUrl(),
            wiremockServer.getMappedPort(8080)
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
}
