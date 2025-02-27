package wiremock;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.*;

import static org.assertj.core.api.Assertions.assertThat;

class WireMockContainerJunit5Test {

    @Container
//    WireMockContainer wiremockServer = new WireMockContainer("2.35.0");
    TestServiceContainer wiremockServer = new TestServiceContainer(Network.newNetwork());

    @Test
    void helloWorld() {
        String body = "{\"accessRights\":{\"read\":true,\"write\":true}}";
        wiremockServer.start();
        var stubber = new TestStubber(wiremockServer);
        TestClient client = new TestClient(wiremockServer);
        stubber.stubTestEndpoint(body);

        String response = client.testCall();

        assertThat(response).isEqualTo(body);
    }
}
