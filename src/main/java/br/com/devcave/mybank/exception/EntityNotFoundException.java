package br.com.devcave.mybank.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends MyBankException {

    public EntityNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Entity not found");
    }
}
