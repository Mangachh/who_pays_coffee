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
    
    /**
     * Guarda un nuevo {@link Member} en la base de datos
     * @param member -> meimbro a guardar
     * @return -> miembro guardado
     * @throws MemberHasNoNicknameException -> si no tiene nickname
     */
    Member saveGroupMember(final Member member) throws MemberHasNoNicknameException;

    
    @Deprecated
    Member saveGroupMember(final RegisteredUser user, final String nickname, boolean isAdmin);

    /**
     * Borra un {@link Member} de la base de datos a partir de su id
     * @param id -> id del miembro a borrar
     */
    void deleteGroupMemberById(final Long id);

    /**
     * Encuentra un {@link Member} a partir de su id
     * @param memberId -> id a buscar
     * @return -> {@link Member}
     * @throws MemberNotInGroup -> si el miembro no existe en el grupo
     */
    Member findMemberById(Long memberId) throws MemberNotInGroup;
        
    /**
     * TODO: no sé hasta que punto lo necesito
     * @param user
     * @return
     */
    List<Group> findAllByRegUserIdAndIsAdminTrue(final RegisteredUser user);

    List<Group> findAllByRegUserIdAndIsAdminFalse(final RegisteredUser user);
    
    /**
     * Encuentra un miembro de un {@link Group} determinado usando {@link Group#id} y su {@link Member#id}
     * @param groupId -> id del grupo
     * @param memberId -> id del miembro
     * @return -> miembro
     * @throws MemberNotInGroup -> si el miembro no existe
     */
    Member findMemberByGroupIdAndRegUserId(final Long groupId, final Long memberId) throws MemberNotInGroup;


    /**
     * Encuentra un miembro de un {@link Group} determinado usando {@link Group#id} y su {@link Member#nickname}
     * @param groupId -> id del grupo
     * @param memberId -> id del miembro
     * @return -> miembro
     * @throws MemberNotInGroup -> si el miembro no existe
     */
    Member findMemberByGroupIdAndNickname(final Long groupId, final String nickname) throws MemberNotInGroup;

}
