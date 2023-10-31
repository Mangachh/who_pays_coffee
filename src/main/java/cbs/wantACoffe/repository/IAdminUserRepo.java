package cbs.wantACoffe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.entity.AdminUser;

/**
 * Repositorio para {@link AdminUser}
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
@Repository
public interface IAdminUserRepo extends JpaRepository<AdminUser, Long>{
    
    Optional<AdminUser> findByUsername(String username);
    
}
