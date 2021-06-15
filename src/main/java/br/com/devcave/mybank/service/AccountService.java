package br.com.devcave.mybank.service;

import br.com.devcave.mybank.domain.entity.Account;
import br.com.devcave.mybank.domain.request.AccountRequest;
import br.com.devcave.mybank.domain.response.AccountResponse;
import br.com.devcave.mybank.exception.EntityAlreadyExistsException;
import br.com.devcave.mybank.exception.EntityNotFoundException;
import br.com.devcave.mybank.mapper.AccountMapper;
import br.com.devcave.mybank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final BankService bankService;

    private final CustomerService customerService;

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    @Transactional
    public AccountResponse create(final AccountRequest request) {
        if (accountRepository.existsByOwnerIdAndBankId(request.getOwnerId(), request.getBankId())) {
            throw new EntityAlreadyExistsException();
        }
        final var owner = customerService.getById(request.getOwnerId());
        final var bank = bankService.getById(request.getBankId());

        final var account = accountRepository.save(Account.builder()
                .owner(owner)
                .bank(bank)
                .build());

        return accountMapper.accountEntityToResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(final Long id) {
        final var account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return accountMapper.accountEntityToResponse(account);
    }
}
