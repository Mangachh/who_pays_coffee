package cbs.wantACoffe.service.group;

import java.util.List;

import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.MemberHasNoNicknameException;
import cbs.wantACoffe.exceptions.MemberNotInGroup;

/**
 * Interfaz para el servicio de miembros de un grupo. 
 * 
 * @author Lluís Cobos Aumatell
 * @version 0.5
 */
public interface IMemberService {
    
    Member saveGroupMember(final Member member) throws MemberHasNoNicknameException;

    
    @Deprecated
    Member saveGroupMember(final RegisteredUser user, final String nickname, boolean isAdmin);

    void deleteGroupMemberById(final Long id);

    Member findMemberById(Long memberId) throws MemberNotInGroup;
        
    List<Group> findAllByRegUserIdAndIsAdminTrue(final RegisteredUser user);

    List<Group> findAllByRegUserIdAndIsAdminFalse(final RegisteredUser user);
    
    Member findMemberByGroupIdAndRegUserId(final Long groupId, final Long memberId) throws MemberNotInGroup;

    Member findMemberByGroupIdAndNickname(final Long groupId, final String nickname) throws MemberNotInGroup;

}
