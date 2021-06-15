package br.com.devcave.mybank.factory;

import br.com.devcave.mybank.domain.entity.Customer;
import br.com.devcave.mybank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.devcave.mybank.configuration.FakerConfiguration.FAKER;

@Component
@RequiredArgsConstructor
public class CustomerFactory {
    private final CustomerRepository customerRepository;

    public Customer create() {
        return create(1).get(0);
    }

    public List<Customer> create(final Integer quantity) {
        return IntStream.range(0, quantity).boxed()
                .map(it -> customerRepository.save(build()))
                .collect(Collectors.toList());
    }

    private Customer build() {
        return Customer.builder()
                .name(FAKER.name().fullName())
                .nationalIdentifier(UUID.randomUUID().toString())
                .build();
    }
}
