package br.com.devcave.mybank.service.executor;

import br.com.devcave.mybank.configuration.TransferRulesProperties;
import br.com.devcave.mybank.domain.TransferRule;
import br.com.devcave.mybank.domain.TransferType;
import br.com.devcave.mybank.domain.entity.Account;
import br.com.devcave.mybank.exception.NotEnoughMoneyException;
import br.com.devcave.mybank.factory.BankFactory;
import br.com.devcave.mybank.factory.CustomerFactory;
import br.com.devcave.mybank.repository.BankRepository;
import br.com.devcave.mybank.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Map;

@Tag("unit")
public class SameBankTransferExecutorTest {
    private final TransferRulesProperties transferRulesProperties =
            new TransferRulesProperties(Map.of(TransferType.SAME_BANK, new TransferRule(BigDecimal.ZERO, null)));

    private final SameBankTransferExecutor executor = new SameBankTransferExecutor(transferRulesProperties);

    private final BankFactory bankFactory = new BankFactory(Mockito.mock(BankRepository.class));

    private final CustomerFactory customerFactory = new CustomerFactory(Mockito.mock(CustomerRepository.class));

    @Test
    public void doTransferSuccessfully() {
        final var bank = bankFactory.build();
        final BigDecimal originAmount = BigDecimal.valueOf(10_000);
        final var origin = Account.builder()
                .bank(bank).owner(customerFactory.build()).amount(originAmount)
                .build();
        final BigDecimal destinationAmount = BigDecimal.valueOf(10_000);
        final var destination = Account.builder()
                .bank(bank).owner(customerFactory.build()).amount(destinationAmount)
                .build();

        final BigDecimal transferAmount = BigDecimal.valueOf(1_000);

        final var transfer = executor.doTransfer(origin, destination, transferAmount);

        Assertions.assertEquals(transferAmount.doubleValue(), transfer.getAmount().doubleValue());
        Assertions.assertEquals(BigDecimal.ZERO.doubleValue(), transfer.getTax().doubleValue());

        Assertions.assertEquals(destinationAmount.add(transferAmount).doubleValue(),
                transfer.getDestination().getAmount().doubleValue());
        Assertions.assertEquals(originAmount.add(transferAmount.negate()).doubleValue(),
                transfer.getOrigin().getAmount().doubleValue());
    }

    @Test
    public void doTransferNotEnoughAmount() {
        final var bank = bankFactory.build();
        final BigDecimal originAmount = BigDecimal.valueOf(1_000);
        final var origin = Account.builder()
                .bank(bank).owner(customerFactory.build()).amount(originAmount)
                .build();
        final BigDecimal destinationAmount = BigDecimal.valueOf(10_000);
        final var destination = Account.builder()
                .bank(bank).owner(customerFactory.build()).amount(destinationAmount)
                .build();

        final BigDecimal transferAmount = BigDecimal.valueOf(2_000);

        try {
            executor.doTransfer(origin, destination, transferAmount);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof NotEnoughMoneyException);
        }
    }
}
