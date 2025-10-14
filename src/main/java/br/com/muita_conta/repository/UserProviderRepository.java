package br.com.muita_conta.repository;

import br.com.muita_conta.model.UserProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProviderRepository extends JpaRepository<UserProvider, Long> {
}
