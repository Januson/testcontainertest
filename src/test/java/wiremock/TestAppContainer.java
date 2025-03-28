package wiremock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class TestAppContainer extends GenericContainer<TestAppContainer> {
    private static final DockerImageName IMAGE = DockerImageName.parse("test-app:latest");

    private static final Logger LOG = LoggerFactory.getLogger(TestAppContainer.class);
    private static final Slf4jLogConsumer CONTAINER_LOG_CONSUMER = new Slf4jLogConsumer(LOG);

    TestAppContainer(Network network, TestServiceContainer service) {
        super(IMAGE);
        this
            // We need to expose a port to be able to add wiremock stubbings later
            .withNetwork(network)
            .withNetworkAliases("wiremock")
            .withEnv("TEST_HOST", "http://%s:%s".formatted(service.getHost(), service.getFirstMappedPort()))
            .waitingFor(Wait.forHttp("/__admin/mappings").forStatusCode(200))
            .withLogConsumer(CONTAINER_LOG_CONSUMER)
        ;
    }
}
