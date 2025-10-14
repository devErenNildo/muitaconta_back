package br.com.muita_conta.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedApiException extends RuntimeException {
    public final HttpStatus statusCode = HttpStatus.UNAUTHORIZED;

    public UnauthorizedApiException(String mensagem){
        super(mensagem);
    }
}
