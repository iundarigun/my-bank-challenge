package br.com.devcave.mybank.domain.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor(onConstructor = @__({@JsonCreator}))
public class CustomerRequest {
    @NotEmpty
    private final String name;

    @NotEmpty
    private final String nationalIdentifier;
}
