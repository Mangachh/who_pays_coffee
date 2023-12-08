package cbs.wantACoffe.service.admin;

import java.util.List;

import cbs.wantACoffe.dto.group.IGroupInfo;
import cbs.wantACoffe.dto.user.IBasicUserInfo;
import cbs.wantACoffe.entity.AdminUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
/**
 * Interfaz de servicio para {@link IAdminService}
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public interface IAdminService {

    /**
     * Añade un nuevo {@link AdminUser} a la base de datos
     * @param admin -> admin a añadir
     * @return
     */
    AdminUser save(AdminUser admin);

    /**
     * Encuentra un {@link AdminUser} a partir de su idsi existe en la base de datos
     * @param id -> id a buscar
     * @return -> {@link AdminUser}
     * @throws UserNotExistsException -> si no existe
     */
    AdminUser getById(Long id) throws UserNotExistsException;
    
    /**
     * Encuentra un {@link AdminUser} a partir de su username si existe en la base de datos
     * @param username -> username a buscar
     * @return -> {@link AdminUser}
     * @throws UserNotExistsException -> lanzada si no existe
     */
    AdminUser getByUsername(String username) throws UserNotExistsException;

    /**
     * Encuentra un {@link AdminUser} a partir de su username y checquea que el password sea correcto
     * @param username -> username a buscar
     * @param password -> password a comprobar
     * @return -> {@link AdminUser}
     * @throws UserNotExistsException -> si no existe el usuario
     * @throws IncorrectPasswordException -> si el password es incorrecto
     */
    AdminUser getByUsernameAndCheckPass(String username, String password)
            throws UserNotExistsException, IncorrectPasswordException;
    
    /**
     * Devuelve una lista con todos los {@link RegisteredUser} usando la interfaz
     * {@link IBasicUserInfo}. Si no hay usuarios, devuelve una lista vacia
     * @return -> Lista de {@link IBasicUserInfo}
     */
    List<IBasicUserInfo> getAllRegisteredUsers();

    /**
     * Devuelve una lista con todos los grupos usando
     * {@link IGroupInfo}
     * @return -> Lista de {@link IGroupInfo}
     */
    List<IGroupInfo> getAllGroupsAndCountMembers();

    /**
     * Contea la totalidad de {@link Group}
     * @return -> número de grupos
     */
    Long countGroups();

    /**
     * Contea la totalidad de {@link RegisteredUser}
     * @return
     */
    Long countRegisteredUsers();
}
