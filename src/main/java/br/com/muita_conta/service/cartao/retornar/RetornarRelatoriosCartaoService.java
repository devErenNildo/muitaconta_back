package br.com.muita_conta.service.cartao.retornar;


import br.com.muita_conta.exception.NotFoundApiException;
import br.com.muita_conta.repository.CartaoCreditoRepository;
import br.com.muita_conta.repository.DespesaCartaoRepository;
import br.com.muita_conta.repository.FaturaRepository;
import br.com.muita_conta.service.user.retornar.RetornarUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetornarRelatoriosCartaoService {

    private final CartaoCreditoRepository cartaoCreditoRepository;
    private final DespesaCartaoRepository despesaCartaoRepository;
    private final FaturaRepository faturaRepository;
    private final RetornarUserService retornarUserService;


    @Transactional(readOnly = true)
    public List<RetornarLimiteResponse> getLimiteDisponivelPorCartao() {
        var user = retornarUserService.getUsuarioAutenticado();
        var cartoes = cartaoCreditoRepository.buscarTodosCartoesComFaturas(user.getConta());


        return cartoes.stream().map(cartao -> new RetornarLimiteResponse(
                cartao.getNome(),
                cartao.getLimite(),
                cartao.getTotalGasto(),
                cartao.getLimiteDisponivel()
        )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RetornarGastosResponse> getDespesasMesAno(int mes, int ano) {
        var user = retornarUserService.getUsuarioAutenticado();

        return despesaCartaoRepository.findAllByUsuarioAndMesAndAno(user.getConta().getId(), mes, ano)
                .stream()
                .map(despesa -> new RetornarGastosResponse(
                        despesa.getDescricao(),
                        despesa.getValor(),
                        despesa.getData(),
                        despesa.getNumeroParcelas(),
                        despesa.getFatura().getCartaoCredito().getNome()
                )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RetornarFaturaCartaoResponse getTotalDevidoMensal(int mes, int ano) {
        var user = retornarUserService.getUsuarioAutenticado();
        BigDecimal totalDevido = faturaRepository.findTotalFaturasByUsuarioAndMesAndAno(user.getConta().getId(), mes, ano);
        return new RetornarFaturaCartaoResponse(mes, ano, totalDevido);
    }

    @Transactional(readOnly = true)
    public RetornarFaturaDetalhadaResponse getFaturaDetalhada(Long cartaoId, int mes, int ano) {

        var faturaComDespesas = faturaRepository.findFaturaDetalhadaByCartaoMesAno(cartaoId, mes, ano)
                .orElseThrow(() -> new NotFoundApiException("Fatura não encontrada para o cartão e período informados."));


        List<DespesaFatura> despesasDTO = faturaComDespesas.getDespesas().stream()
                .map(despesa -> new DespesaFatura(
                        despesa.getDescricao(),
                        despesa.getData(),
                        despesa.getValor()
                ))
                .collect(Collectors.toList());

        return new RetornarFaturaDetalhadaResponse(
                faturaComDespesas.getCartaoCredito().getNome(),
                faturaComDespesas.getMes(),
                faturaComDespesas.getAno(),
                faturaComDespesas.getValorTotal(),
                faturaComDespesas.getStatus(),
                despesasDTO
        );
    }

}
