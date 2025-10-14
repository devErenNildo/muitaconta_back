package br.com.muita_conta.service.token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginEmailSenhaRequest {
    private String email;
    private String senha;
}
