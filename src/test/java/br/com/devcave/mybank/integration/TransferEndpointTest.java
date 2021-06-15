package br.com.devcave.mybank.integration;

import br.com.devcave.mybank.configuration.CleanDatabase;
import br.com.devcave.mybank.controller.handler.ErrorResponse;
import br.com.devcave.mybank.domain.request.TransferRequest;
import br.com.devcave.mybank.domain.response.TransferResponse;
import br.com.devcave.mybank.factory.AccountFactory;
import br.com.devcave.mybank.factory.BankFactory;
import br.com.devcave.mybank.factory.CustomerFactory;
import br.com.devcave.mybank.repository.AccountRepository;
import br.com.devcave.mybank.repository.TransferRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class TransferEndpointTest extends AbstractEndpointTest {

    private final CustomerFactory customerFactory;

    private final BankFactory bankFactory;

    private final AccountFactory accountFactory;

    private final TransferRepository transferRepository;

    private final AccountRepository accountRepository;

    private final CleanDatabase cleanDatabase;

    @BeforeEach
    void beforeEach() {
        cleanDatabase.clean();
    }

    @Test
    @DisplayName("Do transfers same bank successfully")
    void doTransferSameBankSuccessfully() {
        final var bank = bankFactory.create();
        final var origin = accountFactory.create(bank, customerFactory.create(), BigDecimal.valueOf(10_000));
        final var destination = accountFactory.create(bank, customerFactory.create(), BigDecimal.valueOf(10_000));
        final var count = transferRepository.count();

        final var request = new TransferRequest(origin.getId(), destination.getId(), BigDecimal.valueOf(1_000));

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/transfers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", TransferResponse.class);

        Assertions.assertEquals(count + 1, transferRepository.count());
        Assertions.assertEquals(destination.getAmount().add(request.getAmount()).doubleValue(),
                response.getDestination().getAmount().doubleValue());
        Assertions.assertEquals(destination.getAmount().add(request.getAmount()).doubleValue(),
                accountRepository.findById(destination.getId()).get().getAmount().doubleValue());
        Assertions.assertEquals(origin.getAmount().add(request.getAmount().negate()).doubleValue(),
                response.getOrigin().getAmount().doubleValue());
        Assertions.assertEquals(origin.getAmount().add(request.getAmount().negate()).doubleValue(),
                accountRepository.findById(origin.getId()).get().getAmount().doubleValue());
        Assertions.assertEquals(BigDecimal.ZERO.doubleValue(), response.getTax().doubleValue());
    }

    @Test
    @DisplayName("Do transfers same customer successfully")
    void doTransferSameCustomerSuccessfully() {
        final var owner = customerFactory.create();
        final var origin = accountFactory.create(bankFactory.create(), owner, BigDecimal.valueOf(10_000));
        final var destination = accountFactory.create(bankFactory.create(), owner, BigDecimal.valueOf(10_000));
        final var count = transferRepository.count();

        final var request = new TransferRequest(origin.getId(), destination.getId(), BigDecimal.valueOf(1_000));

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/transfers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", TransferResponse.class);

        Assertions.assertEquals(count + 1, transferRepository.count());
        Assertions.assertEquals(destination.getAmount().add(request.getAmount()).doubleValue(),
                response.getDestination().getAmount().doubleValue());
        Assertions.assertEquals(destination.getAmount().add(request.getAmount()).doubleValue(),
                accountRepository.findById(destination.getId()).get().getAmount().doubleValue());
        Assertions.assertEquals(origin.getAmount().add(request.getAmount().negate()).add(BigDecimal.valueOf(1).negate())
                .doubleValue(), response.getOrigin().getAmount().doubleValue());
        Assertions.assertEquals(origin.getAmount().add(request.getAmount().negate()).add(BigDecimal.valueOf(1).negate())
                .doubleValue(), accountRepository.findById(origin.getId()).get().getAmount().doubleValue());
        Assertions.assertEquals(BigDecimal.valueOf(1).doubleValue(), response.getTax().doubleValue());
    }

    @Test
    @DisplayName("Do transfers different bank successfully")
    void doTransferDifferentBankSuccessfully() {
        final var origin = accountFactory.create();
        final var destination = accountFactory.create();
        final var count = transferRepository.count();

        final var request = new TransferRequest(origin.getId(), destination.getId(), BigDecimal.valueOf(1_000));

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/transfers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", TransferResponse.class);

        Assertions.assertEquals(count + 1, transferRepository.count());
        Assertions.assertEquals(destination.getAmount().add(request.getAmount()).doubleValue(),
                response.getDestination().getAmount().doubleValue());
        Assertions.assertEquals(destination.getAmount().add(request.getAmount()).doubleValue(),
                accountRepository.findById(destination.getId()).get().getAmount().doubleValue());
        Assertions.assertEquals(origin.getAmount().add(request.getAmount().negate()).add(BigDecimal.valueOf(5).negate())
                .doubleValue(), response.getOrigin().getAmount().doubleValue());
        Assertions.assertEquals(origin.getAmount().add(request.getAmount().negate()).add(BigDecimal.valueOf(5).negate())
                .doubleValue(), accountRepository.findById(origin.getId()).get().getAmount().doubleValue());
        Assertions.assertEquals(BigDecimal.valueOf(5).doubleValue(), response.getTax().doubleValue());
    }

    @Test
    @DisplayName("Do transfers same account error")
    void doTransferSameAccountError() {
        final var origin = accountFactory.create();
        final var count = transferRepository.count();

        final var request = new TransferRequest(origin.getId(), origin.getId(), BigDecimal.valueOf(1_000));

        final var response = RestAssured.given()
                .body(request)
                .contentType(ContentType.JSON)
                .when()
                .post("/transfers")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .and()
                .extract()
                .jsonPath()
                .getObject("", ErrorResponse.class);

        Assertions.assertEquals(count, transferRepository.count());
        Assertions.assertEquals(1, response.getErrors().size());
        Assertions.assertEquals("sameOriginAndDestination: transfer must be different origin and destination",
                response.getErrors().get(0));
    }
}
