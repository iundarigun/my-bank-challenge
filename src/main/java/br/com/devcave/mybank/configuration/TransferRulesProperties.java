package br.com.devcave.mybank.configuration;

import br.com.devcave.mybank.domain.TransferRule;
import br.com.devcave.mybank.domain.TransferType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

@Getter
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties("transfer")
public class TransferRulesProperties {

    private final Map<TransferType, TransferRule> rules;
}

