package wiremock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Network;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class WireMockContainerJunit5Test {

    TestServiceContainer wiremockServer;

    @BeforeEach
    void setUp() {
        wiremockServer = new TestServiceContainer(Network.newNetwork());
        wiremockServer.start();

        System.out.printf(
            """
                BaseURL: %s
                MapperURL: %s
                Port: 8080 -> %s
                %n
            """,
            wiremockServer.baseUrl(),
            wiremockServer.mappedUrl(),
            wiremockServer.getMappedPort(8080)
        );
        System.out.println("BaseURL: " + wiremockServer.baseUrl());
        System.out.println("MapperURL: " + wiremockServer.mappedUrl());
        System.out.println("Ports: " + String.join(", ", wiremockServer.getPortBindings()));
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
