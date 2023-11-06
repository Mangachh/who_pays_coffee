package cbs.wantACoffe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;

/**
 * Repositorio para {@link Member}
 * 
 * @author Lluís Cobos Aumatell
 * @version 0.5
 */
@Repository
public interface IMemberRepo extends JpaRepository<Member, Long> {
    
    @Deprecated
    List<Group> findGroupByRegUserAndIsAdminTrue(final RegisteredUser user);
    @Deprecated
    List<Group> findGroupByRegUserAndIsAdminFalse(final RegisteredUser user);
}
