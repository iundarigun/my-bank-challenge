package br.com.devcave.mybank.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TransferResponse {
    private final Long id;

    private final AccountResponse origin;

    private final AccountResponse destination;

    private final LocalDateTime transferAt;

    private final BigDecimal amount;

    private final BigDecimal tax;
}
