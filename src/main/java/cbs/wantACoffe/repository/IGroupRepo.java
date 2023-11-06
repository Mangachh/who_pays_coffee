package cbs.wantACoffe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT g.group_id, g.group_name FROM groups g " +
            "LEFT JOIN group_members gm USING(group_id) " +
            "WHERE gm.reg_user_id = :id AND gm.is_admin = :isAdmin", nativeQuery = true)
    List<Group> findAllByMembersAndMembersIsAdminTrue(
            @Param("id") long id,
            @Param("isAdmin") boolean isAdmin);
}

