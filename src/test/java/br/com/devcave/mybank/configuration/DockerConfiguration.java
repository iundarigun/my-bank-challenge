package br.com.devcave.mybank.configuration;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Configuration
public class DockerConfiguration {
    private static final GenericContainer<?> DATABASE = createDatabaseContainer();

    private static final GenericContainer<?> MOCK = createMockContainer();

    private static GenericContainer<?> createDatabaseContainer() {
        GenericContainer<?> container = new GenericContainer<>("postgres");
        container.withExposedPorts(5432);
        container.getPortBindings().add("15432:5432");
        container.addEnv("POSTGRES_USER", "test");
        container.addEnv("POSTGRES_PASSWORD", "test");
        container.waitingFor(Wait.forListeningPort());
        container.start();
        return container;
    }

    private static GenericContainer<?> createMockContainer() {
        GenericContainer<?> container = new GenericContainer<>("iundarigun/mock-ws");
        container.withExposedPorts(1899);
        container.getPortBindings().add("3899:1899");
        container.waitingFor(Wait.forListeningPort());
        container.withClasspathResourceMapping("mockws/", "/home/config", BindMode.READ_ONLY);
        container.addEnv("MOCK_DEFINITIONPATH", "/home/config/");
        container.addEnv("MOCK_FILESPATH", "/home/config/json/");
        container.start();
        return container;
    }
}
