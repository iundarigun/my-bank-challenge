package br.com.devcave.mybank.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerResponse {
    private final Long id;

    private final String name;

    private final String nationalIdentifier;
}
