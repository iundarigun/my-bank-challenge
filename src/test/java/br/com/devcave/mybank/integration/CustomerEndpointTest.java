package br.com.devcave.mybank.integration;

import br.com.devcave.mybank.controller.handler.ErrorResponse;
import br.com.devcave.mybank.domain.request.CustomerRequest;
import br.com.devcave.mybank.domain.response.CustomerResponse;
import br.com.devcave.mybank.factory.CustomerFactory;
import br.com.devcave.mybank.repository.CustomerRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static br.com.devcave.mybank.configuration.FakerConfiguration.FAKER;

@RequiredArgsConstructor
public class CustomerEndpointTest extends AbstractEndpointTest {

    private final CustomerFactory customerFactory;

    private final CustomerRepository customerRepository;

    @BeforeEach
    void beforeEach() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("find by id successfully")
    void findByIdSuccessfully() {
        final var customer = customerFactory.create(1).get(0);

        final var response = RestAssured.given()
                .pathParam("id", customer.getId())
                .when()
                .get("/customers/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", CustomerResponse.class);

        Assertions.assertEquals(customer.getName(), response.getName());
        Assertions.assertEquals(customer.getNationalIdentifier(), response.getNationalIdentifier());
    }

    @Test
    @DisplayName("find by id not found")
    void findByIdNotFound() {
        final var response = RestAssured.given()
                .pathParam("id", FAKER.number().numberBetween(10_000L, 100_000L))
                .when()
                .get("/customers/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", ErrorResponse.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("Entity not found", response.getErrors().get(0));
    }

    @Test
    @DisplayName("Create customer successfully")
    void createSuccessfully() {
        final var request = new CustomerRequest(FAKER.name().fullName(), UUID.randomUUID().toString());
        final var count = customerRepository.count();

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/customers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", CustomerResponse.class);

        Assertions.assertEquals(count + 1, customerRepository.count());
        Assertions.assertEquals(request.getName(), response.getName());
        Assertions.assertEquals(request.getNationalIdentifier(), response.getNationalIdentifier());
    }

    @Test
    @DisplayName("Create customer empty name")
    void createEmptyName() {
        final var request = new CustomerRequest("", UUID.randomUUID().toString());
        final var count = customerRepository.count();

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/customers")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", ErrorResponse.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals(count, customerRepository.count());
        Assertions.assertEquals("name: must not be empty", response.getErrors().get(0));
    }

    @Test
    @DisplayName("Create existing customer")
    void createExistingCustomer() {
        final var bank = customerFactory.create(1).get(0);
        final var request = new CustomerRequest(FAKER.name().fullName(), bank.getNationalIdentifier());
        final var count = customerRepository.count();

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/customers")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", ErrorResponse.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals(count, customerRepository.count());
        Assertions.assertEquals("Entity already exists", response.getErrors().get(0));
    }
}
