package br.com.muita_conta.service.user.criar;

import br.com.muita_conta.enuns.AccountProvider;
import br.com.muita_conta.model.Conta;
import br.com.muita_conta.model.Role;
import br.com.muita_conta.model.User;
import br.com.muita_conta.repository.RoleRepository;
import br.com.muita_conta.repository.UserRepository;
import br.com.muita_conta.service.conta.criar.CriarContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CriarUsuarioService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public void registerNewUser(CriarUsuarioRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Erro: O e-mail já está em uso!");
        }


        var newUser = new User();
        newUser.setNome(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setRoles(Set.of(roleBasic()));
        newUser.setAccountProviders(AccountProvider.LOCAL);

        String hashedPassword = passwordEncoder.encode(registerRequest.getSenha());
        newUser.setSenha(hashedPassword);

        newUser.setConta(new Conta());

        userRepository.save(newUser);

    }

    @Transactional
    public User criarOuLogarUsuarioGoogle(String email, String name, String pictureUrl) {
        var user = userRepository.findByEmail(email).orElseGet(() -> {
           var newUser = User.builder()
                   .nome(name)
                   .email(email)
                   .senha(passwordEncoder.encode(randomSenha()))
                   .urlFotoPerfil(pictureUrl)
                   .roles(Set.of(roleBasic()))
               .build();
           newUser.setAccountProviders(AccountProvider.GOOGLE);
           return newUser;
        });

        var googleProvider = user.getAccountProviders().stream()
                .filter(up -> up.getProvider() == AccountProvider.GOOGLE)
                .findFirst();

        if (googleProvider.isPresent()){
            googleProvider.get().setUltimoLogin();
        } else {
            user.setAccountProviders(AccountProvider.GOOGLE);
        }

        return userRepository.save(user);
    }

    private String randomSenha() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private Role roleBasic () {
        return roleRepository.findByRoleId(Role.Values.BASIC.getRoleId());
    }
}
