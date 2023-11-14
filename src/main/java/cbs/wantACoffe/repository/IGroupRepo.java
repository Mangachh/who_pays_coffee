package cbs.wantACoffe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.TableNames;
import cbs.wantACoffe.dto.group.IGroupInfo;
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
            
   @Query(
        value = "SELECT g.group_name AS groupName, COUNT(gm.id) AS numMembers from " + TableNames.NAME_GROUP + " g " + //
                        "LEFT JOIN " + TableNames.NAME_GROUP_MEMBERS + " gm USING(group_id)" + //
                        "GROUP BY g.group_id; ",
        nativeQuery = true
    )
    List<IGroupInfo> findAllGroupsAndCountMembers();
}

