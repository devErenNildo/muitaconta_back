package br.com.muita_conta.service.token;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginGoogleRequest {
    @NotBlank(message = "O token do Google não pode ser nulo")
    private String googleIdToken;
}
