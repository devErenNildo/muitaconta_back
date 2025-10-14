package br.com.muita_conta.exception;

import org.springframework.http.HttpStatus;

public class EmailApiException extends RuntimeException {
    public final HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

    public EmailApiException(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
}
