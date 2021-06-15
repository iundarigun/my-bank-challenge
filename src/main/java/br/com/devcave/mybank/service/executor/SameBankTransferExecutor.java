package br.com.devcave.mybank.service.executor;

import br.com.devcave.mybank.configuration.TransferRulesProperties;
import br.com.devcave.mybank.domain.TransferType;
import br.com.devcave.mybank.domain.entity.Account;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
class SameBankTransferExecutor extends TransferExecutor {

    SameBankTransferExecutor(final TransferRulesProperties transferRulesProperties) {
        super(transferRulesProperties);
    }

    @Override
    TransferType getTransferType() {
        return TransferType.SAME_BANK;
    }

    @Override
    void validateExtraRules(final Account origin, final Account destination, final BigDecimal amount) {
        // Nothing to validate
    }
}
