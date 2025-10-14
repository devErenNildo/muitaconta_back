package br.com.muita_conta.exception;

import org.springframework.http.HttpStatus;

public class NotFoundApiException extends RuntimeException {
    public final HttpStatus statusCode = HttpStatus.NOT_FOUND;

    public NotFoundApiException(String mensagem){
        super(mensagem);
    }
}
