package br.com.devcave.mybank.configuration;

import br.com.devcave.mybank.repository.AccountRepository;
import br.com.devcave.mybank.repository.BankRepository;
import br.com.devcave.mybank.repository.CustomerRepository;
import br.com.devcave.mybank.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CleanDatabase {
    private final TransferRepository transferRepository;

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    private final BankRepository bankRepository;

    public void clean() {
        transferRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
        bankRepository.deleteAll();
    }
}
