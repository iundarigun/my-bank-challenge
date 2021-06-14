package br.com.devcave.mybank.factory;

import br.com.devcave.mybank.domain.entity.Bank;
import br.com.devcave.mybank.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.devcave.mybank.configuration.FakerConfiguration.FAKER;

@Component
@RequiredArgsConstructor
public class BankFactory {
    private final BankRepository bankRepository;

    public List<Bank> create() {
        return create(FAKER.number().numberBetween(2, 5));
    }

    public List<Bank> create(final Integer quantity) {
        return IntStream.range(0, quantity).boxed()
                .map(it -> bankRepository.save(build()))
                .collect(Collectors.toList());
    }

    private Bank build() {
        return Bank.builder()
                .name(FAKER.company().name())
                .iban(UUID.randomUUID().toString())
                .build();
    }
}
