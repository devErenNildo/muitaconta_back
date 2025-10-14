package br.com.muita_conta.model;

import br.com.muita_conta.enuns.StatusFatura;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_fatura")
public class Fatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int mes;

    private int ano;

    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private StatusFatura status;

    @ManyToOne
    @JoinColumn(name = "id_cartao_credito")
    private CartaoCredito cartaoCredito;

    @OneToMany(mappedBy = "fatura", cascade = CascadeType.ALL)
    private List<DespesaCartao> despesas;
}
