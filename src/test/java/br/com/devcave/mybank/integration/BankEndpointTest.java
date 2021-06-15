package br.com.devcave.mybank.integration;

import br.com.devcave.mybank.configuration.CleanDatabase;
import br.com.devcave.mybank.controller.handler.ErrorResponse;
import br.com.devcave.mybank.domain.entity.Bank;
import br.com.devcave.mybank.domain.request.BankRequest;
import br.com.devcave.mybank.domain.response.BankResponse;
import br.com.devcave.mybank.factory.BankFactory;
import br.com.devcave.mybank.repository.BankRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static br.com.devcave.mybank.configuration.FakerConfiguration.FAKER;

@RequiredArgsConstructor
public class BankEndpointTest extends AbstractEndpointTest {

    private final BankFactory bankFactory;

    private final BankRepository bankRepository;

    private final CleanDatabase cleanDatabase;

    @BeforeEach
    void beforeEach() {
        cleanDatabase.clean();
    }

    @Test
    @DisplayName("find by params bank default successfully")
    void findByParamsDefaultSuccessfully() {
        final var bankList = bankFactory.create(29);

        final var responseList = RestAssured.given()
                .when()
                .get("/banks")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("currentPage", Matchers.equalTo(1))
                .body("totalPages", Matchers.equalTo(2))
                .body("totalElements", Matchers.equalTo(bankList.size()))
                .and()
                .extract()
                .jsonPath()
                .getList("content", BankResponse.class);

        Assertions.assertEquals(20, responseList.size());
        responseList.forEach(
                responseItem -> {
                    Optional<Bank> expectedBank = bankList.stream().filter(it ->
                            it.getId().equals(responseItem.getId())
                                    && it.getName().equals(responseItem.getName())).findFirst();

                    Assertions.assertTrue(expectedBank.isPresent());
                }
        );
    }

    @Test
    @DisplayName("find by params bank page 2, size 10 successfully")
    void findByParamsPage2Size10Successfully() {
        final var bankList = bankFactory.create(29);

        final var responseList = RestAssured.given()
                .queryParam("page", "2")
                .queryParam("size", "10")
                .when()
                .get("/banks")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("currentPage", Matchers.equalTo(2))
                .body("totalPages", Matchers.equalTo(3))
                .body("totalElements", Matchers.equalTo(bankList.size()))
                .and()
                .extract()
                .jsonPath()
                .getList("content", BankResponse.class);

        Assertions.assertEquals(10, responseList.size());
        responseList.forEach(
                responseItem -> {
                    Optional<Bank> expectedBank = bankList.stream().filter(it ->
                            it.getId().equals(responseItem.getId())
                                    && it.getName().equals(responseItem.getName())).findFirst();

                    Assertions.assertTrue(expectedBank.isPresent());
                }
        );
    }

    @Test
    @DisplayName("find by params size bigger exception")
    void findByParamsWrongParams() {
        final var response = RestAssured.given()
                .queryParam("size", "100")
                .when()
                .get("/banks")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .extract()
                .jsonPath()
                .getObject("", ErrorResponse.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("size: must be less than or equal to 50", response.getErrors().get(0));
    }

    @Test
    @DisplayName("Create bank successfully")
    void createSuccessfully() {
        final var request = new BankRequest(FAKER.company().name(), UUID.randomUUID().toString());
        final var count = bankRepository.count();

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/banks")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", BankResponse.class);

        Assertions.assertEquals(count + 1, bankRepository.count());
        Assertions.assertEquals(request.getName(), response.getName());
        Assertions.assertEquals(request.getIban(), response.getIban());
    }

    @Test
    @DisplayName("Create bank empty name")
    void createEmptyName() {
        final var request = new BankRequest("", UUID.randomUUID().toString());
        final var count = bankRepository.count();

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/banks")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", ErrorResponse.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals(count, bankRepository.count());
        Assertions.assertEquals("name: must not be empty", response.getErrors().get(0));
    }

    @Test
    @DisplayName("Create existing bank")
    void createExistingBank() {
        final var bank = bankFactory.create();
        final var request = new BankRequest(FAKER.company().name(), bank.getIban());
        final var count = bankRepository.count();

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/banks")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", ErrorResponse.class);

        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals(count, bankRepository.count());
        Assertions.assertEquals("Entity already exists", response.getErrors().get(0));
    }

}
