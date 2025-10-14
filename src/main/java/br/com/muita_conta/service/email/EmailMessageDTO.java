package br.com.muita_conta.service.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String para;
    private String nome;
    private String codigo;
}
