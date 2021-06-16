package br.com.devcave.mybank.mapper;

import br.com.devcave.mybank.domain.entity.Account;
import br.com.devcave.mybank.domain.request.AccountRequest;
import br.com.devcave.mybank.domain.response.AccountResponse;
import br.com.devcave.mybank.service.BankService;
import br.com.devcave.mybank.service.CustomerService;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {BankMapper.class, CustomerMapper.class})
public abstract class AccountMapper {
    @Autowired
    private BankService bankService;

    @Autowired
    private CustomerService customerService;

    public abstract AccountResponse accountEntityToResponse(Account entity);

    public Account accountRequestToEntity(final AccountRequest request) {
        final var owner = customerService.getById(request.getOwnerId());
        final var bank = bankService.getById(request.getBankId());

        return Account.builder()
                .owner(owner).bank(bank)
                .build();
    }
}
