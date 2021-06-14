package br.com.devcave.mybank.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends MyBankException {

    public EntityAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "Entity already exists");
    }
}
