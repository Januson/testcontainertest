package wiremock;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

public class TestServiceContainer extends GenericContainer<TestServiceContainer> {
    private static final DockerImageName IMAGE = DockerImageName.parse("wiremock/wiremock:latest");

    String NETWORK_ALIAS = "spm";
    int PORT = 8080;

    TestServiceContainer(Network network) {
        super(IMAGE);
        this.withNetwork(network)
            // We need to expose a port to be able to add wiremock stubbings later
            .withExposedPorts(PORT)
            .withNetworkAliases(NETWORK_ALIAS)
        ;
    }

    String baseUrl() {
        return "http://spm:%d".formatted(getMappedPort(PORT));
    }
}
