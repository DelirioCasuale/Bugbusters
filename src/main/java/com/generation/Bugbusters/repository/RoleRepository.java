package com.generation.Bugbusters.repository;

import com.generation.Bugbusters.entity.Role;
import com.generation.Bugbusters.enumeration.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // specifica che questa è un'interfaccia Repository gestita da Spring
public interface RoleRepository extends JpaRepository<Role, Integer> { // id é integer
    
    // ci fornisce automaticamente: findById, save, delete, findAll, etc.
    // possiamo aggiungere metodi personalizzati se necessario o usare query derivati (SPERO DI NON AVERNE BISOGNO)
    
    // trova un ruolo basandosi sul suo nome (enum), ci serve per assegnare 'ROLE_USER' ai nuovi iscritti
    Optional<Role> findByRoleName(RoleName roleName);
}