package br.com.devcave.mybank.mapper;

import br.com.devcave.mybank.domain.entity.Transfer;
import br.com.devcave.mybank.domain.response.TransferResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AccountMapper.class)
public interface TransferMapper {
    TransferResponse transferEntityToResponse(Transfer entity);
}
