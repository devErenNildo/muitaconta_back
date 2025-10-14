package br.com.muita_conta.exception;

import org.springframework.http.HttpStatus;

public class DeleteApiException extends RuntimeException {

    public final HttpStatus statusCode = HttpStatus.UNPROCESSABLE_ENTITY;

    public DeleteApiException(String mensagem){
        super(mensagem);
    }
}
