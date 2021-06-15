package br.com.devcave.mybank.mapper;

import br.com.devcave.mybank.domain.entity.Account;
import br.com.devcave.mybank.domain.response.AccountResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {BankMapper.class, CustomerMapper.class})
public interface AccountMapper {

    AccountResponse accountEntityToResponse(Account entity);
}
