package br.com.muita_conta.service.token;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TokenResponse {
    private String accessToken;
    private Long expiresIn;
    private String refreshToken;
}
