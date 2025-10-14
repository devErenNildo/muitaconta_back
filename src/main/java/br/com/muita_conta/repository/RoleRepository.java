package br.com.muita_conta.repository;

import br.com.muita_conta.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
    Role findByRoleId(Long roleId);
}
