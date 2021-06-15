package br.com.devcave.mybank.integration;

import br.com.devcave.mybank.controller.handler.ErrorResponse;
import br.com.devcave.mybank.domain.request.AccountRequest;
import br.com.devcave.mybank.domain.response.AccountResponse;
import br.com.devcave.mybank.factory.AccountFactory;
import br.com.devcave.mybank.repository.AccountRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static br.com.devcave.mybank.configuration.FakerConfiguration.FAKER;

@RequiredArgsConstructor
public class AccountEndpointTest extends AbstractEndpointTest {

    private final AccountFactory accountFactory;

    private final AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        accountFactory.cleanDatabase();
    }

    @Test
    @DisplayName("find by id successfully")
    void findByIdSuccessfully() {
        final var account = accountFactory.create();

        final var response = RestAssured.given()
                .pathParam("id", account.getId())
                .when()
                .get("/accounts/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", AccountResponse.class);

        Assertions.assertEquals(account.getBank().getName(), response.getBank().getName());
        Assertions.assertEquals(account.getOwner().getName(), response.getOwner().getName());
    }

    @Test
    @DisplayName("find by id not found")
    void findByIdNotFound() {
        final var response = RestAssured.given()
                .pathParam("id", FAKER.number().numberBetween(10_000L, 100_000L))
                .when()
                .get("/accounts/{id}")
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
    @DisplayName("Create account successfully")
    void createSuccessfully() {
        final var account = accountFactory.build();
        final var request = new AccountRequest(account.getOwner().getId(), account.getBank().getId());
        final var count = accountRepository.count();

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", AccountResponse.class);

        Assertions.assertEquals(count + 1, accountRepository.count());
        Assertions.assertEquals(request.getBankId(), response.getBank().getId());
        Assertions.assertEquals(request.getOwnerId(), response.getOwner().getId());
    }

    @Test
    @DisplayName("Create account null owner")
    void createNullOwner() {
        final var request = new AccountRequest(null, 123L);
        final var count = accountRepository.count();

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", ErrorResponse.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals(count, accountRepository.count());
        Assertions.assertEquals("ownerId: must not be null", response.getErrors().get(0));
    }

    @Test
    @DisplayName("Create existing account")
    void createExistingAccount() {
        final var account = accountFactory.create();
        final var request = new AccountRequest(account.getOwner().getId(), account.getBank().getId());
        final var count = accountRepository.count();

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/accounts")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", ErrorResponse.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals(count, accountRepository.count());
        Assertions.assertEquals("Entity already exists", response.getErrors().get(0));
    }
}
