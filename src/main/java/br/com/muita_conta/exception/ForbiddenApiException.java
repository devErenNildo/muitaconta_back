package br.com.muita_conta.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenApiException extends RuntimeException {
    public final HttpStatus statusCode = HttpStatus.FORBIDDEN;

    public ForbiddenApiException(String mensagem){
        super(mensagem);
    }
}
