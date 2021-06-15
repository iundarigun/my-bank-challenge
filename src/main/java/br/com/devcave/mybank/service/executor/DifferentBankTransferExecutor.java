package br.com.devcave.mybank.service.executor;

import br.com.devcave.mybank.client.EuropeanCentralBankClient;
import br.com.devcave.mybank.configuration.TransferRulesProperties;
import br.com.devcave.mybank.domain.TransferType;
import br.com.devcave.mybank.domain.entity.Account;
import br.com.devcave.mybank.exception.DestinationRestrictionsException;
import br.com.devcave.mybank.exception.EuropeanCentralBankNotAvailableException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
class DifferentBankTransferExecutor extends TransferExecutor {

    private final EuropeanCentralBankClient europeanCentralBankClient;

    DifferentBankTransferExecutor(final EuropeanCentralBankClient europeanCentralBankClient,
                                  final TransferRulesProperties transferRulesProperties) {
        super(transferRulesProperties);
        this.europeanCentralBankClient = europeanCentralBankClient;
    }

    @Override
    TransferType getTransferType() {
        return TransferType.DIFFERENT_BANK;
    }

    @Override
    void validateExtraRules(final Account origin, final Account destination, final BigDecimal amount) {
        try {
            final var response = europeanCentralBankClient
                    .hasCustomerRestrictions(destination.getOwner().getNationalIdentifier());
            if (response.getRestrictions()) {
                throw new DestinationRestrictionsException();
            }
        } catch (FeignException | CallNotPermittedException ex) {
            log.warn("Error consulting ECB. Circuit breaker Open={}",
                    ex instanceof CallNotPermittedException, ex);
            throw new EuropeanCentralBankNotAvailableException();
        }
    }
}
