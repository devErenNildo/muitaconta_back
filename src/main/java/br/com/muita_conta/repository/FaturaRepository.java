package br.com.muita_conta.repository;

import br.com.muita_conta.model.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FaturaRepository extends JpaRepository<Fatura, Long> {

    @Query(value = """
        SELECT * FROM
            tb_fatura
        WHERE
            id_cartao_credito = :idCartao
        AND
           mes = :mes
       AND
        ano = :ano
    """, nativeQuery = true)
    Optional<Fatura> buscarFaturaDeMesAno(
            @Param("id_cartao_credito") Long idCartaoCredito,
            @Param("mes") int mes,
            @Param("ano") int ano
    );

// ====================================================================================================

    @Query(value = """
        SELECT * FROM
            tb_fatura
        WHERE id_cartao_credito = :id_cartao
    """, nativeQuery = true)
    List<Fatura> buscarFaturasDeCartao(@Param("id_cartao") Long idCartao);

// ====================================================================================================

    @Query(value = """
            SELECT COALESCE(SUM(f.valor_total), 0) FROM
                tb_fatura f
            JOIN
                tb_cartao_credito cc
            ON
                f.id_cartao_credito = cc.id
            WHERE
                cc.id_conta = :idConta
            AND
                f.mes = :mes AND f.ano = :ano
    """, nativeQuery = true)
    BigDecimal findTotalFaturasByUsuarioAndMesAndAno(
            @Param("idConta") Long idConta,
            @Param("mes") int mes,
            @Param("ano") int ano
    );

// ====================================================================================================

    @Query("SELECT f FROM Fatura f LEFT JOIN FETCH f.despesas WHERE f.cartaoCredito.id = :cartaoId AND f.mes = :mes AND f.ano = :ano")
    Optional<Fatura> findFaturaDetalhadaByCartaoMesAno(
            @Param("cartaoId") Long cartaoId,
            @Param("mes") int mes,
            @Param("ano") int ano
    );
}
