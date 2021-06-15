package br.com.devcave.mybank.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountResponse {
    private final Long id;

    private final BankResponse bank;

    private final CustomerResponse owner;

    private final BigDecimal amount;
}
