package br.com.muita_conta.service.cartao.criar;

import br.com.muita_conta.model.CartaoCredito;
import br.com.muita_conta.repository.CartaoCreditoRepository;
import br.com.muita_conta.service.user.retornar.RetornarUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CriarCartaoService {

    private final CartaoCreditoRepository cartaoCreditoRepository;
    private final RetornarUserService retornarUserService;

    public void criarCartao (CriarCartaoRequest request) {

        var user = retornarUserService.getUsuarioAutenticado();

        var cartao = CartaoCredito.builder()
                .nome(request.getNome())
                .limite(request.getLimite())
                .diaFechamento(request.getDiaFechamento())
                .diaVencimento(request.getDiaVencimento())
                .conta(user.getConta())
            .build();

        cartaoCreditoRepository.save(cartao);

    }
}
