package br.com.muita_conta.exception;

import org.springframework.http.HttpStatus;

public class InternalApiException extends RuntimeException {

    public final HttpStatus statusCode = HttpStatus.INTERNAL_SERVER_ERROR;

    public InternalApiException(Throwable cause){
        super(cause);
    }
    public InternalApiException(String message) {
        super(message);
    }
}
