package br.com.muita_conta.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Table
@Entity(name = "tb_balanco_mensal")
public class BalancoSaldoMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;

    @Column(nullable = false)
    private int ano;

    @Column(nullable = false)
    private int mes;

    @Column(name = "total", nullable = false)
    private BigDecimal total = BigDecimal.ZERO;
}
