package br.com.muita_conta.service.user.retornar;

import br.com.muita_conta.exception.UserNotFoundException;
import br.com.muita_conta.model.User;
import br.com.muita_conta.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetornarUserService {

    private final UserRepository userRepository;

    public User getUsuarioAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            String userId = jwt.getSubject();
            return userRepository.findById(Long.valueOf(userId))
                    .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado, tente novamente."));
        } else {
            throw new IllegalStateException("Aconteceu um erro inesperado ao validar seu pedido tente novamente.");
        }
    }
}
