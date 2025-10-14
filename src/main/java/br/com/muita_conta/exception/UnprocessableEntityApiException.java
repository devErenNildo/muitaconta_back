package br.com.muita_conta.exception;

import org.springframework.http.HttpStatus;

public class UnprocessableEntityApiException extends RuntimeException {
    public final HttpStatus statusCode = HttpStatus.UNPROCESSABLE_ENTITY;

    public UnprocessableEntityApiException(String mensagem){
        super(mensagem);
    }
}
