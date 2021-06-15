package br.com.devcave.mybank.service.executor;

import br.com.devcave.mybank.configuration.TransferRulesProperties;
import br.com.devcave.mybank.domain.TransferType;
import br.com.devcave.mybank.domain.entity.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
class SameCustomerTransferExecutor extends TransferExecutor {

    SameCustomerTransferExecutor(final TransferRulesProperties transferRulesProperties) {
        super(transferRulesProperties);
    }

    @Override
    TransferType getTransferType() {
        return TransferType.SAME_CUSTOMER;
    }

    @Override
    void validateExtraRules(final Account origin, final Account destination, final BigDecimal amount) {
        // Nothing to validate
    }
}
