package br.com.devcave.mybank.exception;

import org.springframework.http.HttpStatus;

public class NotEnoughMoneyException extends MyBankException {

    public NotEnoughMoneyException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "Account doesn't have enough money");
    }
}
