package br.com.devcave.mybank.factory;

import br.com.devcave.mybank.domain.entity.Account;
import br.com.devcave.mybank.domain.entity.Bank;
import br.com.devcave.mybank.domain.entity.Customer;
import br.com.devcave.mybank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static br.com.devcave.mybank.configuration.FakerConfiguration.FAKER;

@Component
@RequiredArgsConstructor
public class AccountFactory {
    private final AccountRepository accountRepository;

    private final CustomerFactory customerFactory;

    private final BankFactory bankFactory;

    public Account create() {
        return create(1).get(0);
    }

    public Account create(final Bank bank, final Customer owner, final BigDecimal amount) {
        return accountRepository.save(Account.builder()
                .bank(bank)
                .owner(owner)
                .amount(amount)
                .build());
    }

    public List<Account> create(final Integer quantity) {
        return IntStream.range(0, quantity).boxed()
                .map(it -> accountRepository.save(build()))
                .collect(Collectors.toList());
    }

    public Account build() {
        final var bank = bankFactory.create();
        final var owner = customerFactory.create();
        return Account.builder()
                .bank(bank)
                .owner(owner)
                .amount(BigDecimal
                        .valueOf(FAKER.number().randomDouble(2, 10_000, 20_000)))
                .build();
    }
}
