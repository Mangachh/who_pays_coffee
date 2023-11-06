package cbs.wantACoffe.service.group;

import java.util.List;

import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;

/**
 * Interfaz para el servicio de miembros de un grupo. 
 * 
 * @author Lluís Cobos Aumatell
 * @version 0.5
 */
public interface IMemberService {
    
    Member saveGroupMember(final Member member);

    Member saveGroupMember(final RegisteredUser user, final String nickname, boolean isAdmin);

    Member deleteGroupMemberById(final Long id);

    Member updateNickname(final String newNickname);
    
    Member addGroupMember(final Member regUser);
    
    Member deleteGroupMember(final Long id);

    List<Group> findAllByRegUserIdAndIsAdminTrue(final RegisteredUser user);
    List<Group> findAllByRegUserIdAndIsAdminFalse(final RegisteredUser user);

}
