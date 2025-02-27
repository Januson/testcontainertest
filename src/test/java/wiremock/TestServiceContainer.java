package wiremock;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

public class TestServiceContainer extends GenericContainer<TestServiceContainer> {
    DockerImageName image = DockerImageName.parse("wiremock/wiremock:latest");

    String NETWORK_ALIAS = "spm";
    int PORT = 8080;

    TestServiceContainer(Network network) {
        super(DockerImageName.parse("wiremock/wiremock:latest"));
        this.withNetwork(network)
            // We need to expose a port to be able to add wiremock stubbings later
            .withExposedPorts(PORT)
            .withNetworkAliases(NETWORK_ALIAS)

//        withFileSystemBind(
//            Paths.get(".", "infrastructure", "mappings").toAbsolutePath().toString(),
//            "/home/wiremock/mappings",
//            BindMode.READ_ONLY,
//        )
//        withFileSystemBind(
//            Paths.get(".", "infrastructure", "files").toAbsolutePath().toString(),
//            "/home/wiremock/__files",
//            BindMode.READ_ONLY,
//        )
        ;
    }

    String baseUrl() {
        return "http://%s:%d".formatted(NETWORK_ALIAS, PORT);
    }

    String mappedUrl() {
        return "http://localhost:%d".formatted(getMappedPort(PORT));
    }
}

