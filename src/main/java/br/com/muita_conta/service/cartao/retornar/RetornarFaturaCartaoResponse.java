package br.com.muita_conta.service.cartao.retornar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetornarFaturaCartaoResponse {
    private int mes;
    private int ano;
    private BigDecimal valorFatura;
}
