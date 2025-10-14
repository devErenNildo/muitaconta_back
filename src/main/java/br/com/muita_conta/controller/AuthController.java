package br.com.muita_conta.controller;

import br.com.muita_conta.service.token.*;
import br.com.muita_conta.service.user.criar.CriarUsuarioRequest;
import br.com.muita_conta.service.user.criar.CriarUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;
    private final CriarUsuarioService criarUsuarioService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginEmailSenhaRequest dto) {
        return ResponseEntity.status(HttpStatus.OK).body(loginService.loginEmailSenha(dto));
    }

    @PostMapping("/google")
    public ResponseEntity<TokenResponse> googleLogin(
            @RequestBody @Valid LoginGoogleRequest request
    ) throws GeneralSecurityException, IOException, IllegalArgumentException {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(loginService.loginGoogle(request));
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<TokenResponse> refreshToken(@RequestBody @Valid TokenRefreshRequest request) {
//        var response = tokenService.renovarToken(request.getRefreshToken());
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/register")
    public void registerUser(@RequestBody @Valid CriarUsuarioRequest registerRequest) {
        criarUsuarioService.registerNewUser(registerRequest);
    }
}
