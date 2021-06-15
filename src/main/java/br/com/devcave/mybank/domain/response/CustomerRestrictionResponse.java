package br.com.devcave.mybank.domain.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(onConstructor = @__({@JsonCreator}))
public class CustomerRestrictionResponse {
    private final Boolean restrictions;
}
