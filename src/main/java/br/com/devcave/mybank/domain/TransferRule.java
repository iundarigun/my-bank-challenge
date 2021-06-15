package br.com.devcave.mybank.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.math.BigDecimal;

@Getter
@ConstructorBinding
@AllArgsConstructor
public class TransferRule {
    private final BigDecimal tax;

    private final BigDecimal limit;
}
