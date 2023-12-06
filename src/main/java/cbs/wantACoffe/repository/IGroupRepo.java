package cbs.wantACoffe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cbs.wantACoffe.dto.group.IGroupInfo;
import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
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

    @Query(
            value= "SELECT g." + Group.COLUMN_ID_NAME + ", g." + Group.COLUMN_GROUP_NAME + ", g." + Group.COLUMN_OWNER_ID +
            " FROM " + Group.TABLE_NAME + " g" + 
            " LEFT JOIN " + Member.TABLE_NAME + " gm USING(" + Group.COLUMN_ID_NAME + ")" +
                    " WHERE gm. " + Member.COLUMN_REG_USER_ID_NAME + " = :regUserId " +
                    " AND gm." + Member.COLUMN_IS_ADMIN_NAME + " = :isAdmin", 
            nativeQuery = true)
    List<Group> findAllByMembersAndMembersIsAdmin(
            @Param("regUserId") long regUserId,
            @Param("isAdmin") boolean isAdmin);
            
    @Query(
        value = "SELECT g. " + Group.COLUMN_GROUP_NAME + " AS groupName, COUNT(gm." + Member.COLUMN_ID_NAME + ") AS numMembers" +
        " FROM " + Group.TABLE_NAME + " g" + 
        " LEFT JOIN " + Member.TABLE_NAME + " gm USING (" + Member.COLUMN_GROUP_ID_NAME + ")" + 
        "GROUP BY g." + Group.COLUMN_ID_NAME ,
        nativeQuery = true
    )
    List<IGroupInfo> findAllGroupsAndCountMembers();


    
}

