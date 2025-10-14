package br.com.muita_conta.controller;

import br.com.muita_conta.service.cartao.criar.CriarCartaoRequest;
import br.com.muita_conta.service.cartao.criar.CriarCartaoService;
import br.com.muita_conta.service.cartao.despesas.CriarDespesaRequest;
import br.com.muita_conta.service.cartao.despesas.DespesasCartaoService;
import br.com.muita_conta.service.cartao.retornar.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartao")
@RequiredArgsConstructor
public class CartaoController {

    private final DespesasCartaoService despesasCartaoService;
    private final CriarCartaoService criarCartaoService;
    private final RetornarRelatoriosCartaoService retornarRelatoriosCartaoService;

    @PostMapping("/adicionar")
    public void adicionarNovoCartao(@RequestBody CriarCartaoRequest request) {
        criarCartaoService.criarCartao(request);
    }

    @PostMapping("/compra")
    public void adicionarDespesaCartao(@RequestBody CriarDespesaRequest request) {
        despesasCartaoService.lancarDespesa(request);
    }

    @GetMapping("/limite")
    public ResponseEntity<List<RetornarLimiteResponse>> retornarLimiteDisponivel() {
        return ResponseEntity.ok(retornarRelatoriosCartaoService.getLimiteDisponivelPorCartao());
    }

    @GetMapping("/despesas")
    public ResponseEntity<List<RetornarGastosResponse>> getDespesas(
            @RequestParam @NotNull(message = "O parâmetro 'mes' é obrigatório.")
            @Min(value = 1, message = "O mês deve ser entre 1 e 12.")
            @Max(value = 12, message = "O mês deve ser entre 1 e 12.")
            Integer mes,


            @RequestParam @NotNull(message = "O parâmetro 'ano' é obrigatório.") Integer ano
    ){
        return ResponseEntity.ok(retornarRelatoriosCartaoService.getDespesasMesAno(mes, ano));
    }

    @GetMapping("/total-devido")
    public ResponseEntity<RetornarFaturaCartaoResponse> getTotalDevido(
            @RequestParam @NotNull(message = "O parâmetro 'mes' é obrigatório.")
            @Min(value = 1, message = "O mês deve ser entre 1 e 12.")
            @Max(value = 12, message = "O mês deve ser entre 1 e 12.")
            Integer mes,

            @RequestParam @NotNull(message = "O parâmetro 'ano' é obrigatório.")
            Integer ano
    ){
        return ResponseEntity.ok(retornarRelatoriosCartaoService.getTotalDevidoMensal(mes, ano));
    }

    @GetMapping("/fatura-detalhada/{cartaoId}")
    public ResponseEntity<RetornarFaturaDetalhadaResponse> getFaturaDetalhada(
            @PathVariable Long cartaoId,

            @RequestParam @NotNull(message = "O parâmetro 'mes' é obrigatório.")
            @Min(value = 1, message = "O mês deve ser entre 1 e 12.")
            @Max(value = 12, message = "O mês deve ser entre 1 e 12.")
            Integer mes,

            @RequestParam @NotNull(message = "O parâmetro 'ano' é obrigatório.")
            Integer ano
    ) {
        return ResponseEntity.ok(retornarRelatoriosCartaoService.getFaturaDetalhada(cartaoId, mes, ano));
    }



}
