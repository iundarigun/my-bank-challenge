package br.com.devcave.mybank.configuration;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Configuration
public class DockerConfiguration {
    private static final GenericContainer<?> DATABASE = createDatabaseContainer();

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
}
