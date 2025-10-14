package br.com.muita_conta.service.token;

import br.com.muita_conta.service.user.criar.CriarUsuarioService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final TokenService tokenService;
    private final CriarUsuarioService criarUsuarioService;

    @Value("${google.client-id}")
    private String googleClientId;

    public TokenResponse loginEmailSenha(LoginEmailSenhaRequest request) {
        var user = tokenService.validarUser(request.getEmail(), request.getSenha());

        return tokenService.gerarTokenJwt(user);
    }

    public TokenResponse loginGoogle(LoginGoogleRequest request) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = validarGoogleIdToken(request);
        if (idToken == null) {
            throw new IllegalArgumentException("Token do Google inválido!");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        var user = criarUsuarioService.criarOuLogarUsuarioGoogle(email, name, pictureUrl);

        return tokenService.gerarTokenJwt(user);
    }

    private GoogleIdToken validarGoogleIdToken(LoginGoogleRequest idTokenString)  throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new GsonFactory()
        ).setAudience(Collections.singleton(googleClientId))
        .build();

        return verifier.verify(idTokenString.getGoogleIdToken());
    }
}
