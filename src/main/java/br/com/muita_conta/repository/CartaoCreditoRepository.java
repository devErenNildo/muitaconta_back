package br.com.muita_conta.repository;

import br.com.muita_conta.model.CartaoCredito;
import br.com.muita_conta.model.Conta;
import br.com.muita_conta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, Long> {

    @Query("SELECT c FROM CartaoCredito c LEFT JOIN FETCH c.faturas WHERE c.id = :id")
    Optional<CartaoCredito> buscarCartaoComFaturas(@Param("id") Long id);


    @Query("SELECT c FROM CartaoCredito c LEFT JOIN FETCH c.faturas WHERE c.conta = :conta")
    List<CartaoCredito> buscarTodosCartoesComFaturas(@Param("conta") Conta conta);
}
