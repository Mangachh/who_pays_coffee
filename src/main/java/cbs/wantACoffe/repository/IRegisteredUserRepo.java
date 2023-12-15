package cbs.wantACoffe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.dto.user.IBasicUserInfo;
import cbs.wantACoffe.entity.RegisteredUser;


/**
 * Interfaz para el repositori de User.
 * Hereda de JpaRepository
 * 
 * @author Lluís Cobos
 * @version 1.0
 */
@Repository
public interface IRegisteredUserRepo extends JpaRepository<RegisteredUser, Long> {

    Optional<RegisteredUser> findByUsername(String username);

    Optional<RegisteredUser> findByEmail(String email);

    @Query(
        value = "SELECT " + RegisteredUser.COLUMN_EMAIL_NAME + ", " + RegisteredUser.COLUMN_USERNAME_NAME +
                " FROM " + RegisteredUser.TABLE_NAME, 
        nativeQuery = true)
    List<IBasicUserInfo> findAllBasicData();    
    
}
