package br.com.devcave.mybank.mapper;

import br.com.devcave.mybank.domain.entity.Bank;
import br.com.devcave.mybank.domain.request.BankRequest;
import br.com.devcave.mybank.domain.response.BankResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface BankMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    Bank bankRequestToEntity(BankRequest request);

    BankResponse bankEntityToResponse(Bank entity);
}
