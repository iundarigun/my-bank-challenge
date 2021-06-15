package br.com.devcave.mybank;

import br.com.devcave.mybank.configuration.TransferRulesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = TransferRulesProperties.class)
public class MyBankApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MyBankApplication.class, args);
    }

}
