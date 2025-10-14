package br.com.muita_conta.service.cartao.retornar;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiltroMesAnoRequest {

    @NotNull(message = "O mês é obrigatório")
    @Min(value = 1, message = "O mês deve ser um valor entre 1 e 12")
    @Max(value = 12, message = "O mês deve ser um valor entre 1 e 12")
    private Integer mes;

    @NotNull(message = "O ano é obrigatório")
    private Integer ano;

}
