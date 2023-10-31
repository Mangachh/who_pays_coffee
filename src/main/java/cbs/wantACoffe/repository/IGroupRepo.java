package cbs.wantACoffe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.RegisteredUser;

/**
 * Repositorio para {@link Group}
 * 
 * @author Lluís Cobos Aumatell
 * @version 0.5
 */
@Repository
public interface IGroupRepo extends JpaRepository<Group, Long> {

    
        
    List<Group> findAllByMembersRegUser(RegisteredUser user);

    List<Group> findAllByMembersIsAdmin(RegisteredUser user);
}
