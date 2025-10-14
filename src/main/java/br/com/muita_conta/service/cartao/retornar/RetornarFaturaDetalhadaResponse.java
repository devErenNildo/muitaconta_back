package br.com.muita_conta.service.cartao.retornar;

import br.com.muita_conta.enuns.StatusFatura;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetornarFaturaDetalhadaResponse {
    private String nomeCartao;
    private int mes;
    private int ano;
    private BigDecimal valorTotal;
    private StatusFatura status;
    private List<DespesaFatura> despesas;
}
