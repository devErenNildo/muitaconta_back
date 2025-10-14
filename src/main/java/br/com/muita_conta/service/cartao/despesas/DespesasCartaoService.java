package br.com.muita_conta.service.cartao.despesas;

import br.com.muita_conta.enuns.StatusFatura;
import br.com.muita_conta.exception.CardNotFoundException;
import br.com.muita_conta.exception.LimiteIndisponivelException;
import br.com.muita_conta.model.CartaoCredito;
import br.com.muita_conta.model.DespesaCartao;
import br.com.muita_conta.model.Fatura;
import br.com.muita_conta.repository.CartaoCreditoRepository;
import br.com.muita_conta.repository.DespesaCartaoRepository;
import br.com.muita_conta.repository.FaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DespesasCartaoService {

    private final DespesaCartaoRepository despesaCartaoRepository;
    private final FaturaRepository faturaRepository;
    private final CartaoCreditoRepository cartaoCreditoRepository;

    public void lancarDespesa(CriarDespesaRequest request) {
        var cartao = cartaoCreditoRepository.buscarCartaoComFaturas(request.getIdCartao())
                .orElseThrow(() -> new CardNotFoundException("Cartão de crédito não encontrado"));

        if (!cartao.temLimiteDisponivel(request.getValor())) {
            throw new LimiteIndisponivelException("Limite indisponivel no cartão de crédito !");
        }

        var dataCompra = request.getData() == null
                ? LocalDate.now()
                : request.getData();

        int fechamento = cartao.getDiaFechamento();

        var dataFatura = dataCompra.getDayOfMonth() >= fechamento
                ? dataCompra.plusMonths(1)
                : dataCompra;

        int mesFatura = dataFatura.getMonthValue();
        int anoFatura = dataFatura.getYear();

        List<Fatura> faturasExistentes = faturaRepository.buscarFaturasDeCartao(cartao.getId());

        Map<String, Fatura> mapaDeFaturas = faturasExistentes.stream()
                .collect(Collectors.toMap(f -> f.getAno() + "-" + f.getMes(), Function.identity()));

        var novasDespesas = new ArrayList<DespesaCartao>();

        // Compra parcelada
        if (request.getNumeroParcelas() != null & request.getNumeroParcelas() > 1) {
            for (int i = 0; i < request.getNumeroParcelas(); i++) {
                var dataCompraParcela = request.getData().plusMonths(i);

                var dataCompetenciaFatura =  dataCompraParcela.getDayOfMonth() >= cartao.getDiaFechamento()
                        ? dataCompraParcela.plusMonths(1)
                        : dataCompraParcela;

                String chaveFatura = dataCompetenciaFatura.getYear() + "-" + dataCompetenciaFatura.getMonthValue();

                var faturaDaParcela = mapaDeFaturas.computeIfAbsent(chaveFatura, key -> {
                    var newFatura = Fatura.builder()
                            .mes(dataCompetenciaFatura.getMonthValue())
                            .ano(dataCompetenciaFatura.getYear())
                            .valorTotal(BigDecimal.ZERO)
                            .status(StatusFatura.ABERTA)
                            .cartaoCredito(cartao)
                        .build();
                    return newFatura;
                });

                criarDespesaParaFatura(request, i, faturaDaParcela, novasDespesas);
                mapaDeFaturas.put(chaveFatura, faturaDaParcela);
            }
        // Compra a vista
        } else {
            var dataCompetenciaFatura = request.getData().getDayOfMonth() >= cartao.getDiaFechamento() ?
                    request.getData().plusMonths(1) : request.getData();
            String chaveFatura = dataCompetenciaFatura.getYear() + "-" + dataCompetenciaFatura.getMonthValue();

            Fatura fatura = mapaDeFaturas.computeIfAbsent(chaveFatura, key -> {
                var newFatura = Fatura.builder()
                        .mes(dataCompetenciaFatura.getMonthValue())
                        .ano(dataCompetenciaFatura.getYear())
                        .valorTotal(BigDecimal.ZERO)
                        .status(StatusFatura.ABERTA)
                        .cartaoCredito(cartao)
                    .build();
                return newFatura;
            });
            criarDespesaParaFatura(request, 0, fatura, novasDespesas);
            mapaDeFaturas.put(chaveFatura, fatura);
        }

        faturaRepository.saveAll(mapaDeFaturas.values());
        despesaCartaoRepository.saveAll(novasDespesas);
    }

    private void criarDespesaParaFatura(CriarDespesaRequest request, int parcelaAtual, Fatura fatura, List<DespesaCartao> novasDespesas) {
        var despesa = new DespesaCartao();
        String descricao = request.getNumeroParcelas() != null && request.getNumeroParcelas() > 1 ?
                String.format("%s (%d/%d)", request.getDescricao(), parcelaAtual + 1, request.getNumeroParcelas()) :
                request.getDescricao();

        despesa.setDescricao(descricao);
        despesa.setValor(request.getValor());
        despesa.setData(request.getData());
        despesa.setNumeroParcelas(request.getNumeroParcelas() != null ? request.getNumeroParcelas() : 1);
        despesa.setFatura(fatura);

        novasDespesas.add(despesa);
        fatura.setValorTotal(fatura.getValorTotal().add(request.getValor()));
    }
}
