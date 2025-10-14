package br.com.muita_conta.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class LimiteIndisponivelException extends RuntimeException {
    public LimiteIndisponivelException(String message) {
        super(message);
    }
}
