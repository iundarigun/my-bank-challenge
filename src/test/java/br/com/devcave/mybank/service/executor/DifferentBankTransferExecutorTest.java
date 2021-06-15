package br.com.devcave.mybank.service.executor;

import br.com.devcave.mybank.client.EuropeanCentralBankClient;
import br.com.devcave.mybank.configuration.TransferRulesProperties;
import br.com.devcave.mybank.domain.TransferRule;
import br.com.devcave.mybank.domain.TransferType;
import br.com.devcave.mybank.domain.entity.Account;
import br.com.devcave.mybank.domain.response.CustomerRestrictionResponse;
import br.com.devcave.mybank.exception.DestinationRestrictionsException;
import br.com.devcave.mybank.exception.EuropeanCentralBankNotAvailableException;
import br.com.devcave.mybank.exception.NotEnoughMoneyException;
import br.com.devcave.mybank.exception.TransferLimitExceededException;
import br.com.devcave.mybank.factory.BankFactory;
import br.com.devcave.mybank.factory.CustomerFactory;
import br.com.devcave.mybank.repository.BankRepository;
import br.com.devcave.mybank.repository.CustomerRepository;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Map;

@Tag("unit")
public class DifferentBankTransferExecutorTest {
    private final EuropeanCentralBankClient europeanCentralBankClient = Mockito.mock(EuropeanCentralBankClient.class);

    private final TransferRulesProperties transferRulesProperties =
            new TransferRulesProperties(Map.of(TransferType.DIFFERENT_BANK,
                    new TransferRule(BigDecimal.valueOf(10), BigDecimal.valueOf(5_000))));

    private final DifferentBankTransferExecutor executor =
            new DifferentBankTransferExecutor(europeanCentralBankClient, transferRulesProperties);

    private final BankFactory bankFactory = new BankFactory(Mockito.mock(BankRepository.class));

    private final CustomerFactory customerFactory = new CustomerFactory(Mockito.mock(CustomerRepository.class));

    @BeforeEach
    void beforeEach() {
        Mockito.reset(europeanCentralBankClient);
    }

    @Test
    public void doTransferSuccessfully() {
        Mockito.when(europeanCentralBankClient.hasCustomerRestrictions(Mockito.any()))
                .thenReturn(new CustomerRestrictionResponse(false));

        final BigDecimal originAmount = BigDecimal.valueOf(10_000);
        final var origin = Account.builder()
                .bank(bankFactory.build())
                .owner(customerFactory.build()).amount(originAmount)
                .build();
        final BigDecimal destinationAmount = BigDecimal.valueOf(10_000);
        final var destination = Account.builder()
                .bank(bankFactory.build()).owner(customerFactory.build()).amount(destinationAmount)
                .build();

        final BigDecimal transferAmount = BigDecimal.valueOf(1_000);

        final var transfer = executor.doTransfer(origin, destination, transferAmount);

        Assertions.assertEquals(transferAmount.doubleValue(), transfer.getAmount().doubleValue());
        Assertions.assertEquals(BigDecimal.valueOf(10).doubleValue(), transfer.getTax().doubleValue());

        Assertions.assertEquals(destinationAmount.add(transferAmount).doubleValue(),
                transfer.getDestination().getAmount().doubleValue());
        Assertions.assertEquals(originAmount.add(transferAmount.negate()).add(BigDecimal.valueOf(10).negate()).doubleValue(),
                transfer.getOrigin().getAmount().doubleValue());
    }

    @Test
    public void doTransferNotEnoughAmount() {
        final BigDecimal originAmount = BigDecimal.valueOf(1_000);
        final var origin = Account.builder()
                .bank(bankFactory.build())
                .owner(customerFactory.build()).amount(originAmount)
                .build();
        final BigDecimal destinationAmount = BigDecimal.valueOf(10_000);
        final var destination = Account.builder()
                .bank(bankFactory.build()).owner(customerFactory.build()).amount(destinationAmount)
                .build();

        final BigDecimal transferAmount = BigDecimal.valueOf(2_000);

        try {
            executor.doTransfer(origin, destination, transferAmount);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof NotEnoughMoneyException);
        }
    }

    @Test
    public void doTransferNotEnoughAmountForTax() {
        final BigDecimal originAmount = BigDecimal.valueOf(1_000);
        final var origin = Account.builder()
                .bank(bankFactory.build())
                .owner(customerFactory.build()).amount(originAmount)
                .build();
        final BigDecimal destinationAmount = BigDecimal.valueOf(10_000);
        final var destination = Account.builder()
                .bank(bankFactory.build()).owner(customerFactory.build()).amount(destinationAmount)
                .build();

        final BigDecimal transferAmount = BigDecimal.valueOf(1_000);

        try {
            executor.doTransfer(origin, destination, transferAmount);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof NotEnoughMoneyException);
        }
    }

    @Test
    public void doTransferLimitExceeded() {
        final BigDecimal originAmount = BigDecimal.valueOf(50_000);
        final var origin = Account.builder()
                .bank(bankFactory.build())
                .owner(customerFactory.build()).amount(originAmount)
                .build();
        final BigDecimal destinationAmount = BigDecimal.valueOf(50_000);
        final var destination = Account.builder()
                .bank(bankFactory.build()).owner(customerFactory.build()).amount(destinationAmount)
                .build();

        final BigDecimal transferAmount = BigDecimal.valueOf(11_000);

        try {
            executor.doTransfer(origin, destination, transferAmount);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof TransferLimitExceededException);
        }
    }

    @Test
    public void doTransferWithAPIOutOfService() {
        Mockito.when(europeanCentralBankClient.hasCustomerRestrictions(Mockito.any()))
                .thenThrow(Mockito.mock(FeignException.class));

        final BigDecimal originAmount = BigDecimal.valueOf(10_000);
        final var origin = Account.builder()
                .bank(bankFactory.build())
                .owner(customerFactory.build()).amount(originAmount)
                .build();
        final BigDecimal destinationAmount = BigDecimal.valueOf(10_000);
        final var destination = Account.builder()
                .bank(bankFactory.build()).owner(customerFactory.build()).amount(destinationAmount)
                .build();

        final BigDecimal transferAmount = BigDecimal.valueOf(1_000);

        try {
            executor.doTransfer(origin, destination, transferAmount);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof EuropeanCentralBankNotAvailableException);
        }
    }

    @Test
    public void doTransferWithRestrictions() {
        Mockito.when(europeanCentralBankClient.hasCustomerRestrictions(Mockito.any()))
                .thenReturn(new CustomerRestrictionResponse(true));

        final BigDecimal originAmount = BigDecimal.valueOf(10_000);
        final var origin = Account.builder()
                .bank(bankFactory.build())
                .owner(customerFactory.build()).amount(originAmount)
                .build();
        final BigDecimal destinationAmount = BigDecimal.valueOf(10_000);
        final var destination = Account.builder()
                .bank(bankFactory.build()).owner(customerFactory.build()).amount(destinationAmount)
                .build();

        final BigDecimal transferAmount = BigDecimal.valueOf(1_000);

        try {
            executor.doTransfer(origin, destination, transferAmount);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof DestinationRestrictionsException);
        }
    }
}
