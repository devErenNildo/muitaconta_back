package br.com.muita_conta.model;

import br.com.muita_conta.enuns.AccountProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_user_provider")
public class UserProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AccountProvider provider;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    public void setUltimoLogin() {
        this.ultimoLogin = LocalDateTime.now();
    }
}
