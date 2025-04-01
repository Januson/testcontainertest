package wiremock;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class TestServiceContainer extends GenericContainer<TestServiceContainer> {
    private static final DockerImageName IMAGE = DockerImageName.parse("wiremock/wiremock:latest");
    int PORT = 8080;

    TestServiceContainer(Network network) {
        super(IMAGE);
        this
            // We need to expose a port to be able to add wiremock stubbings later
            .withNetwork(network)
            .withNetworkAliases("wiremock")
            .withExposedPorts(PORT)
            .waitingFor(Wait.forHttp("/__admin/mappings").forStatusCode(200));
        ;
    }

    String baseUrl() {
        return "http://%s:%d".formatted(
            this.getHost(),
            this.getMappedPort(PORT));
    }
}
