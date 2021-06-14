package br.com.devcave.mybank.mapper;

import br.com.devcave.mybank.domain.entity.Customer;
import br.com.devcave.mybank.domain.request.CustomerRequest;
import br.com.devcave.mybank.domain.response.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "accountList", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    Customer customerRequestToEntity(CustomerRequest request);

    CustomerResponse customerEntityToResponse(Customer entity);
}
