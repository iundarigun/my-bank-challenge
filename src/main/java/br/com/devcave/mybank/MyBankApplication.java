package br.com.devcave.mybank;

import br.com.devcave.mybank.configuration.TransferRulesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties(value = TransferRulesProperties.class)
public class MyBankApplication {

    public static void main(final String[] args) {
        SpringApplication.run(MyBankApplication.class, args);
    }

}
