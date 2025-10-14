package br.com.muita_conta.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_cartao_credito")
public class CartaoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private BigDecimal limite;

    private Integer diaFechamento;

    private Integer diaVencimento;

    @ManyToOne
    @JoinColumn(name = "id_conta")
    private Conta conta;

    @OneToMany(mappedBy = "cartaoCredito", cascade = CascadeType.ALL)
    private List<Fatura> faturas;

    // Métodos para auxiliar no lançamento despesas

    public BigDecimal getTotalGasto() {
        if (this.faturas == null || this.faturas.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return faturas.stream()
                .map(Fatura::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getLimiteDisponivel() {
        return this.limite.subtract(getTotalGasto());
    }

    public boolean temLimiteDisponivel(BigDecimal valorCompra) {
        return getLimiteDisponivel().compareTo(valorCompra) >= 0;
    }
}
