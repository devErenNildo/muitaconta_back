package br.com.muita_conta.service.cartao.criar;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CriarCartaoRequest {

    @NotBlank(message = "O nome do cartão é obrigatório")
    private String nome;

    @NotNull(message = "O limite do cartão é obrigatório")
    @Positive(message = "O limite deve ser um valor positivo")
    private BigDecimal limite;

    @NotNull(message = "O dia de fechamento é obrigatório")
    @Min(value = 1, message = "O dia de fechamento deve ser entre 1 e 31")
    @Max(value = 31, message = "O dia de fechamento deve ser entre 1 e 31")
    private Integer diaFechamento;

    @NotNull(message = "O dia de vencimento é obrigatório")
    @Min(value = 1, message = "O dia de vencimento deve ser entre 1 e 31")
    @Max(value = 31, message = "O dia de vencimento deve ser entre 1 e 31")
    private Integer diaVencimento;
}
