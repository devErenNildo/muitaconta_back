package br.com.muita_conta.service.cartao.retornar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetornarGastosResponse {
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;
    private Integer numeroParcelas;
    private String nomeCartao;
}
