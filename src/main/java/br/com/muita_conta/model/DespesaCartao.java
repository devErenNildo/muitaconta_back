package br.com.muita_conta.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "tb_despesa_cartao")
public class DespesaCartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private BigDecimal valor;

    private LocalDate data;

    private Integer numeroParcelas;

    @ManyToOne
    @JoinColumn(name = "id_fatura")
    private Fatura fatura;
}
