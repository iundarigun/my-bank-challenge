package br.com.devcave.mybank.domain.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor(onConstructor = @__({@JsonCreator}))
public class AccountRequest {
    @NotNull
    private final Long ownerId;

    @NotNull
    private final Long bankId;
}
