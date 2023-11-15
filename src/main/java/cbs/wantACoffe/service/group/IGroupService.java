package cbs.wantACoffe.service.group;

import java.util.List;

import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;

/**
 * Interfaz para el servicio de gestión de grupos
 * 
 * @author Lluís Cobos Aumatell
 * @version 0.5
 */
public interface IGroupService {
    
    /**
     * Añade un nuevo grupo 
     * @param group -> grupo a añadir
     * @return
     */
    Group saveGroup(final Group group);

    /**
     * Elimina un grupo con todos sus integrantes
     * @param group -> id del grupo a eliminar
     */
    void deleteGroup(long id);

    /**
     * Devuelve un grupo a partir de su id
     * @param id -> id del grupo a buscar
     * @return -> grupo
     * @throws Exception -> TODO: excepciones concreta
     */
    Group findGroupById(final Long id) throws Exception;

    /**
     * Encuentra todos los grupos de los que es miembro un usuario.
     * TODO: no implementado, sujeto a cambios
     * @param id
     * @return
     */
    List<Group> findAllByMemberId(final Long id);

    /**
     * Devuelve la lista de miembros de un grupo determinado
     * 
     * @return
     */
    List<Member> findAllGroupMembers();
    
    
    /**
     * Añade un miembro al grupo
     * TODO: no implementado
     * @param user
     * @return
     */
    Member addGroupUser(final Member user);

    /**
     * Devuelve todos los {@link RegisteredUser} de un grupo
     * @param user
     * @return
     */
    List<Group> findAllByRegUser(final RegisteredUser user);

    List<Group> findAllByRegUserIsAdmin(final RegisteredUser user, boolean isAdmin);

    /**
     * Intenta añadir un miembro al grupo
     * @param member
     * @param group
     */
    void tryAddMemberToGroup(final Member member, final Group group);

    boolean isUserInGroup();

    

}
