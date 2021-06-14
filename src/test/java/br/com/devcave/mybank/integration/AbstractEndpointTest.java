package br.com.devcave.mybank.integration;

import io.restassured.RestAssured;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractEndpointTest {

    @Getter
    @LocalServerPort
    private Integer port;

    @BeforeEach
    public void before() {
        RestAssured.port = port;
    }
}
