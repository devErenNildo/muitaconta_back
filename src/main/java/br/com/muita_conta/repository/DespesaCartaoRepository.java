package br.com.muita_conta.repository;

import br.com.muita_conta.model.DespesaCartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DespesaCartaoRepository extends JpaRepository<DespesaCartao, Long> {


    @Query(value = "SELECT dc.* FROM tb_despesa_cartao dc " +
            "JOIN tb_fatura f ON dc.id_fatura = f.id " +
            "JOIN tb_cartao_credito cc ON f.id_cartao_credito = cc.id " +
            "WHERE cc.id_conta = :contaId AND f.mes = :mes AND f.ano = :ano"
    , nativeQuery = true)
    List<DespesaCartao> findAllByUsuarioAndMesAndAno(
            @Param("contaId") Long contaId,
            @Param("mes") int mes,
            @Param("ano") int ano
    );
}
