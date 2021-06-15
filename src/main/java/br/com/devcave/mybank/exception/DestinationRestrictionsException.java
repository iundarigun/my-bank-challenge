package br.com.devcave.mybank.exception;

import org.springframework.http.HttpStatus;

public class DestinationRestrictionsException extends MyBankException {

    public DestinationRestrictionsException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Destination has restrictions on ECB");
    }
}
