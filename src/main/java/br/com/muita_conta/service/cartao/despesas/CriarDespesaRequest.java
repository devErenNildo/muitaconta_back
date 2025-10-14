package br.com.muita_conta.service.cartao.despesas;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CriarDespesaRequest {

    private String descricao;

    private BigDecimal valor;

    private LocalDate data;

    @NotNull
    @Min(1)
    private Integer numeroParcelas;

    private Long idCartao;
}
