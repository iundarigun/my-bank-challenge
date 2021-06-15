package br.com.devcave.mybank.client;

import br.com.devcave.mybank.domain.response.CustomerRestrictionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "bceClient", url = "${feign.client.config.bceClient.url}")
public interface EuropeanCentralBankClient {

    @CircuitBreaker(name = "circuitbreaker-restrictions")
    @GetMapping("customer-restrictions/{nationalIdentifier}")
    CustomerRestrictionResponse hasCustomerRestrictions(@PathVariable String nationalIdentifier);
}
