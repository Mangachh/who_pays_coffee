package cbs.wantACoffe.service.user;

import cbs.wantACoffe.entity.RegisteredUser;
import cbs.wantACoffe.exceptions.IncorrectPasswordException;
import cbs.wantACoffe.exceptions.NullValueInUserDataException;
import cbs.wantACoffe.exceptions.UserNotExistsException;
import cbs.wantACoffe.exceptions.UsernameEmailAlreadyExistsException;

/**
 * Interfaz para todos los servicios de usuario
 * 
 * @author Lluís Cobos Aumatell
 * @version 1.0
 */
public interface IRegisteredUserService {

    /**
     * Registra un nuevo usuario en la base de datos
     * @param user -> el usuario a registrar
     * @return -> datos del usuario
     * @throws NullValueInUserDataException -> si alguno de los datos es null, se lanza excepción
     * @throws UsernameEmailAlreadyExistsException -> si ya existe el username o el email, se lanza
     * 
     * @see RegisteredUser
     */
    RegisteredUser saveNewUser(RegisteredUser user) throws NullValueInUserDataException, UsernameEmailAlreadyExistsException;
    
    /**
     * Borra a un usuario de la base de datos a partir de su id
     * @param id -> id del usuario a borrar
     */
    void deleteUserById(Long id);

    /**
     * Borra un usuario a partir de sí mismo
     * @param user -> usuario a borrar
     */
    void deleteUser(RegisteredUser user);
    
    /**
     * Busca un usuario a partir del campo <b>username</b>.
     * <p>
     * Como el username es único para cada <b>User</b> sólo nos puede devolver 
     * un resultado
     * @param username -> nombre del usuario a buscar
     * @return -> el usuario
     * @throws UserNotExistsException -> si el usuario no existe se lanza
     */
    RegisteredUser findByUsername(String username) throws UserNotExistsException;

    /**
     * Busca un usuario a partir del campo <b>email</b>.
     * <p>
     * Como el email es úico para cada <b>User</b> sólo nos devolverá un resultado
     * @param email -> email del usuario
     * @return -> usuario 
     * @throws UserNotExistsException -> si el usuario no existe, se lanza
     */
    RegisteredUser findByEmail(String email) throws UserNotExistsException;

    /**
     * Busca un usuario a partir del campo <b>id</>
     * <p>
     * Como el id es úico para cada <b>User</b> sólo nos devolverá un resultado
     * @param id -> id que buscar
     * @return -> usuario
     * @throws UserNotExistsException -> si el usuaro no existe, se lanza
     */
    RegisteredUser findById(Long id) throws UserNotExistsException;

    /**
     * Busca un usuario a partir de su <b>username</>; si existe
     * entonces comprueba que el <b>password</> almacenado en la base
     * de datos corresponde al que pasamos por parametros. 
     * Si es así, se devuelve el usuario.
     * @param username -> user a buscar
     * @param password -> passwor del usuario
     * @return user
     * @throws UserNotExistsException > se lanza si no existe el <b>User</>
     * @throws IncorrectPasswordException > se lanza si el <b>password</b> almacenado en la
     *                                       base de datos no corresponde con el pasado por
     *                                       parametro.
     */
    RegisteredUser findByEmailAndCheckPass(final String username, final String password) throws UserNotExistsException, IncorrectPasswordException;
}
