package cbs.wantACoffe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    List<Group> findGroupByRegUserAndIsAdminTrue(final RegisteredUser user);

    List<Group> findGroupByRegUserAndIsAdminFalse(final RegisteredUser user);

    @Query(value = "SELECT * FROM " + Member.TABLE_NAME +
            " WHERE " + Member.COLUMN_GROUP_ID_NAME + " =:groupId " +
            "AND " + Member.COLUMN_REG_USER_ID_NAME + " = :userId", nativeQuery = true)
    Optional<Member> findMemberByGroupIdAndRegUserId(
            @Param("groupId") final Long groupId,
            @Param("userId") final Long userId);

    @Query(value = "SELECT * FROM " + Member.TABLE_NAME +
            " WHERE " + Member.COLUMN_GROUP_ID_NAME + " =:groupId " +
            "AND " + Member.COLUMN_NICKNAME_NAME + " = :nickname", nativeQuery = true)
    Optional<Member> findMemberByGroupIdAndNickname(
            @Param("groupId") final Long groupId,
            @Param("nickname") final String nickname);

    List<Member> findAllMembersByGroupGroupId(long id);
}
