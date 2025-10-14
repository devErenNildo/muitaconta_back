package br.com.muita_conta.service.token;

import br.com.muita_conta.model.RefreshToken;
import br.com.muita_conta.model.Role;
import br.com.muita_conta.model.User;
import br.com.muita_conta.repository.RefreshTokenRepository;
import br.com.muita_conta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration-ms}")
    private Long refreshTokenDurationMs;



    @Transactional
    public TokenResponse renovarToken(String refreshToken) {
        return findByToken(refreshToken)
                .map(this::verificarExpiracaoToken)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = gerarTokenJwt(user).getAccessToken();
                    RefreshToken newRefreshToken = gerarRefreshToken(user);
                    return new TokenResponse(newAccessToken, 3600L, newRefreshToken.getToken());
                })
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado ou inválido!"));
    }

    @Transactional
    protected User validarUser (String email, String senha) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos!"));

        if (!user.isPasswordCorrect(senha, passwordEncoder)) {
            throw new RuntimeException("Email ou senha inválidos!");
        }

        return user;
    }

    @Transactional
    protected TokenResponse gerarTokenJwt(User user) {
        var now = Instant.now();
        long expiresIn = 3600L;

        var scopes = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("muitaconta-api")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        var refreshToken = gerarRefreshToken(user);

        return new TokenResponse(token, 3600L, refreshToken.getToken());
    }

    @Transactional
    protected RefreshToken gerarRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    private Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verificarExpiracaoToken(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado. Por favor, faça login novamente.");
        }
        return token;
    }
}
