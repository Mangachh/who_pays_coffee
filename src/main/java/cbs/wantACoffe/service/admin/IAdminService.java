package cbs.wantACoffe.service.admin;

import java.util.List;

import cbs.wantACoffe.dto.user.BasicUserInfo;
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

    AdminUser save(AdminUser admin);

    AdminUser findById(Long id) throws UserNotExistsException;
    
    AdminUser findByUsername(String username) throws UserNotExistsException;

    AdminUser findByUsernameAndCheckPass(String username, String password) throws UserNotExistsException, IncorrectPasswordException;
    
    List<BasicUserInfo> findAllRegisteredUsers();

    Long countGroups();

    Long countRegisteredUsers();
}
