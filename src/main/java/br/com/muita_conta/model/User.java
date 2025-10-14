package br.com.muita_conta.model;

import br.com.muita_conta.enuns.AccountProvider;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user", unique = true, nullable = false)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "senha", nullable = true)
    private String senha;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private Conta conta;

    @Column(name = "url_foto_perfil", nullable = true )
    private String urlFotoPerfil;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "tb_users_roles",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_role")
    )
    private Set<Role> roles;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserProvider> accountProviders = new HashSet<>();

    public boolean isPasswordCorrect(String plainPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(plainPassword, this.senha);
    }

    public void setAccountProviders(AccountProvider provider) {
        var newAccountProvider = UserProvider.builder()
                .user(this)
                .provider(provider)
                .ultimoLogin(LocalDateTime.now())
                .build();

        this.accountProviders.add(newAccountProvider);
    }

    public void setConta(Conta conta) {
        this.conta = conta;
        conta.setUser(this);
    }

}
