package cbs.wantACoffe.service.group;

import java.util.List;

import cbs.wantACoffe.entity.Group;
import cbs.wantACoffe.entity.Member;
import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.GroupHasNoNameException;
import cbs.wantACoffe.exceptions.GroupNotExistsException;

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
     * @throws GroupHasNoNameException
     */
    Group saveGroup(final Group group) throws GroupHasNoNameException;

    /**
     * Elimina un grupo con todos sus integrantes
     * @param group -> id del grupo a eliminar
     */
    void deleteGroup(long id);

    /**
     * Devuelve un grupo a partir de su id
     * @param id -> id del grupo a buscar
     * @return -> grupo
     * @throws GroupNotExistsException -> Lanzada si el grupo no existe
     */
    Group getGroupById(final Long id) throws GroupNotExistsException;
    

    /**
     * Devuelve todos los {@link RegisteredUser} de un grupo
     * @param user
     * @return
     */
    List<Group> getAllByRegUser(final RegisteredUser user);


    /**
     * Devuelve todos los {@link RegisteredUser} de un grupo de los cuales
     * es admin o no
     * @param user
     * @param isAdmin -> si queremos sacar SÍ admin, ponemos true,
     *                   si queremos donde NO admin, ponemos false
     * @return
     */
    List<Group> getAllByRegUserIsAdmin(final RegisteredUser user, boolean isAdmin);


    /**
     * Devuelve todos los {@link RegisteredUser} de un grupo de los cuales
     * es dueño
     * @param user
     * @return
     */
    List<Group> getAllByOwner(final Long regUserId);


    /**
     * Intenta añadir un miembro al grupo
     * @param member
     * @param group
     */
    void tryAddMemberToGroup(final Member member, final Group group);   

}
