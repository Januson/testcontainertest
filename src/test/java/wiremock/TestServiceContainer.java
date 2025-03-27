package wiremock;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class TestServiceContainer extends GenericContainer<TestServiceContainer> {
    private static final DockerImageName IMAGE = DockerImageName.parse("wiremock/wiremock:latest");
    int PORT = 8080;

    TestServiceContainer() {
        super(IMAGE);
        this
            // We need to expose a port to be able to add wiremock stubbings later
            .withExposedPorts(PORT)
        ;
    }

    String baseUrl() {
        return "http://localhost:%d".formatted(getMappedPort(PORT));
    }
}
