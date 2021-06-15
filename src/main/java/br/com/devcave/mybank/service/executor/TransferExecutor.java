package br.com.devcave.mybank.service.executor;

import br.com.devcave.mybank.configuration.TransferRulesProperties;
import br.com.devcave.mybank.domain.TransferRule;
import br.com.devcave.mybank.domain.TransferType;
import br.com.devcave.mybank.domain.entity.Account;
import br.com.devcave.mybank.domain.entity.Transfer;
import br.com.devcave.mybank.exception.NotEnoughMoneyException;
import br.com.devcave.mybank.exception.TransferLimitExceededException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public abstract class TransferExecutor {
    private final TransferRulesProperties transferRulesProperties;

    abstract TransferType getTransferType();

    abstract void validateExtraRules(Account origin, Account destination, BigDecimal amount);

    public Transfer doTransfer(final Account origin, final Account destination, final BigDecimal amount) {
        final var now = LocalDateTime.now();
        final var totalAmount = calculateTotalAmount(amount);

        if (origin.getAmount().compareTo(totalAmount) < 0) {
            throw new NotEnoughMoneyException();
        }

        validateLimit(amount);

        validateExtraRules(origin, destination, amount);

        origin.setAmount(origin.getAmount().add(totalAmount.negate()));
        destination.setAmount(destination.getAmount().add(amount));

        return Transfer.builder()
                .transferAt(now)
                .amount(amount)
                .tax(getTransferRule().getTax())
                .destination(destination)
                .origin(origin)
                .build();
    }

    private BigDecimal calculateTotalAmount(final BigDecimal amount) {
        if (getTransferRule().getTax() != null) {
            return amount.add(getTransferRule().getTax());
        }
        return amount;
    }

    private void validateLimit(final BigDecimal amount) {
        if (getTransferRule().getLimit() != null && getTransferRule().getLimit().compareTo(amount) < 0) {
            throw new TransferLimitExceededException();
        }
    }

    TransferRule getTransferRule() {
        return transferRulesProperties.getRules().get(getTransferType());
    }

}
